package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.impl.db.mapper.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.utility.SqlQueries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.stream.Collectors;

@Component("filmDbStorage")
@Slf4j
public class FilmDbStorage extends DBStorage implements FilmStorage {

    private final UserStorage userStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate,
                         @Qualifier("userDbStorage") UserStorage userStorage) {
        super(jdbcTemplate);
        this.userStorage = userStorage;
    }

    @Override
    public Film createFilm(Film film) {
        addFilmToDb(film);
        updateFilmGenres(film);
        updateFilmDirectors(film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        getFilmById(film.getId());
        jdbcTemplate.update(SqlQueries.UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        jdbcTemplate.update(SqlQueries.DELETE_FILMS_GENRES, film.getId());
        updateFilmGenres(film);
        jdbcTemplate.update(SqlQueries.DELETE_FILMS_DIRECTORS, film.getId());
        updateFilmDirectors(film);
        return film;
    }

    @Override
    public Film getFilmById(int filmId) {
        Film film = jdbcTemplate.query(SqlQueries.GET_FILM, new FilmResultSetExtractor(), filmId).stream().findAny().orElse(null);
        if (film == null) {
            throw new FilmNotFoundException("Film with id " + filmId + " doesn't exist");
        }
        return film;
    }

    @Override
    public List<Film> getAllFilms() {
        return jdbcTemplate.query(SqlQueries.GET_FILMS,
                new FilmResultSetExtractor());
    }

    @Override
    public List<Film> getFilmsWhereIdEquals(List<Integer> filmsIds) {
        String inSql = String.join(",", Collections.nCopies(filmsIds.size(), "?"));
        String sql = String.format(SqlQueries.GET_FILMS + " WHERE f.FILM_ID IN (%s)", inSql);
        return jdbcTemplate.query(sql, new FilmResultSetExtractor(), filmsIds.toArray());
    }

    @Override
    public void addScore(int filmId, int userId, int score) {
        userStorage.getUserById(userId);
        getFilmById(filmId);
        jdbcTemplate.update(SqlQueries.ADD_SCORE, filmId,  userId, score);
    }

    @Override
    public void deleteScore(int filmId, int userId) {
        userStorage.getUserById(userId);
        getFilmById(filmId);
        jdbcTemplate.update(SqlQueries.DELETE_SCORE, filmId, userId);
    }

    @Override
    public List<Film> findFilmsByTitle(String query) {
        return jdbcTemplate.query(SqlQueries.FIND_FILMS_BY_NAME,
                new FilmResultSetExtractor(), "%" + query + "%");
    }

    @Override
    public List<Film> getPopularFilmsByGenreIdAndYear(int count, Integer genreId, Integer year) {
        List<Film> popularFilms;
        if (Objects.isNull(genreId) & Objects.isNull(year)) {
            popularFilms = jdbcTemplate.query(SqlQueries.GET_POPULAR_FILMS,
                    new FilmResultSetExtractor());
        } else if (!Objects.isNull(genreId) & !Objects.isNull(year)) {
            popularFilms = jdbcTemplate.query(SqlQueries.GET_POPULAR_FILMS_BY_GENRE_ID_AND_YEAR,
                    new FilmResultSetExtractor(), genreId, year);
        } else if (!Objects.isNull(genreId) & Objects.isNull(year)) {
            popularFilms = jdbcTemplate.query(SqlQueries.GET_POPULAR_FILMS_BY_GENRE_ID,
                    new FilmResultSetExtractor(), genreId);
        } else {
            popularFilms = jdbcTemplate.query(SqlQueries.GET_POPULAR_FILMS_BY_YEAR,
                    new FilmResultSetExtractor(), year);
        }
        return popularFilms.stream().limit(count).collect(Collectors.toList());
    }

    @Override
    public List<Film> findFilmsByDirector(String query) {
        return jdbcTemplate.query(SqlQueries.FIND_FILMS_BY_DIRECTOR,
                new FilmResultSetExtractor(), "%" + query + "%");
    }

    @Override
    public List<Film> findFilmsByTitleOrDirector(String query) {
        return jdbcTemplate.query(SqlQueries.FIND_FILMS_BY_NAME_OR_DIRECTOR,
                new FilmResultSetExtractor(), "%" + query + "%", "%" + query + "%");
    }

    @Override
    public List<Film> findAllFilmsByDirectorSortedByYearOrScores(int directorId, String sortBy) {
        String sqlQuery;
        switch (sortBy) {
            case "YEAR":
                sqlQuery = SqlQueries.FIND_ALL_FILMS_BY_DIRECTOR_SORTED_BY_YEAR;
                break;
            case "SCORES":
            default:
                sqlQuery = SqlQueries.FIND_ALL_FILMS_BY_DIRECTOR_SORTED_BY_SCORES;
        }

        List<Film> foundFilms = jdbcTemplate.query(sqlQuery, new FilmResultSetExtractor(), directorId);
        if (foundFilms.isEmpty()) {
            throw new DirectorNotFoundException(String.format("Films with director id \"%d\" not found", directorId));
        }

        return foundFilms;
    }

    @Override
    public List<Film> getFilmsScoredByUser(int userId) {
        return jdbcTemplate.query(SqlQueries.GET_USERS_SCORES,
                new FilmResultSetExtractor(), userId);
    }

    @Override
    public void getFilmScoresStats(
            Map<Integer, HashMap<Integer, Double>> inputData,
            List<Integer> allFilmsIds
    ) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SqlQueries.GET_SCORES);

        while (rs.next()) {
            int userDbId = rs.getInt("USER_ID");
            int filmDbId = rs.getInt("FILM_ID");
            double filmDbScore = rs.getDouble("SCORE");

            HashMap<Integer, Double> scores = inputData.getOrDefault(userDbId, new HashMap<>());
            scores.put(filmDbId, filmDbScore);
            inputData.put(userDbId, scores);

            if (!allFilmsIds.contains(filmDbId)) {
                allFilmsIds.add(filmDbId);
            }
        }
    }

   //todo реализовать метод getFilmsLikeWithScoreByUser
   /* public List<Film> getFilmsLikeWithScoreByUser(int userId) {
        jdbcTemplate.query(SqlQueries.GET_SCORES_BY_USER_ID, new FilmResultSetExtractor(), userId);

    }*/

    @Override
    public void deleteFilmById(int filmId) {
        if (getFilmById(filmId) == null) {
            throw new FilmNotFoundException("Film with id " + filmId + " doesn't exist");
        }
        jdbcTemplate.update(SqlQueries.DELETE_FILMS_BY_ID, filmId);
    }

    private void updateFilmDirectors(Film film) {
        Set<Director> filmDirectors = film.getDirectors();
        if (filmDirectors == null) {
            film.setDirectors(new HashSet<>());
            return;
        }
        filmDirectors.forEach(director ->
                jdbcTemplate.update(SqlQueries.ADD_FILMS_DIRECTORS, film.getId(), director.getId()));
    }

    private void updateFilmGenres(Film film) {
        List<Genre> filmGenres = film.getGenres();
        if (filmGenres == null) {
            film.setGenres(new ArrayList<>());
            return;
        }
        List<Genre> filmGenresWithoutDuplicates = new ArrayList<>(new LinkedHashSet<>(film.getGenres()));
        film.setGenres(filmGenresWithoutDuplicates);
        for (Genre genre : filmGenresWithoutDuplicates) {
            jdbcTemplate.update(SqlQueries.ADD_FILMS_GENRES, film.getId(), genre.getId());
        }
    }

    private void addFilmToDb(Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SqlQueries.ADD_FILM, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
    }

    public void clearDb() {
        jdbcTemplate.update("DELETE FROM APP_USERS");
        jdbcTemplate.update("DELETE FROM FILMS");
        jdbcTemplate.update("DELETE FROM FILMS_GENRES");
        jdbcTemplate.update("DELETE FROM SCORES");
        jdbcTemplate.update("DELETE FROM FRIENDS");
    }
}