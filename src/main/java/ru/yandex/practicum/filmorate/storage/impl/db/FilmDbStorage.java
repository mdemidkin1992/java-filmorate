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
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.impl.db.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.storage.impl.db.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.impl.db.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.impl.db.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.utility.SqlQueries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Component("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final UserStorage userStorage;

    public FilmDbStorage(JdbcTemplate jdbcTemplate, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.jdbcTemplate = jdbcTemplate;
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
        Film film = jdbcTemplate.query(SqlQueries.GET_FILM, new FilmMapper(), filmId).stream().findAny().orElse(null);
        if (film == null) {
            log.error("Film with id {} doesn't exist", filmId);
            throw new FilmNotFoundException("Film with id " + filmId + " doesn't exist");
        }

        Rating mpa = getFilmRating(filmId);
        List<Genre> genres = getFilmGenres(filmId);
        Collection<Director> directors = getFilmDirectors(filmId);

        film.setMpa(mpa);
        film.setGenres(genres);
        film.getDirectors().addAll(directors);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        List<Film> allFilms = jdbcTemplate.query(SqlQueries.GET_FILMS, new FilmMapper());
        getGenres(allFilms);
        getRatings(allFilms);
        getDirectors(allFilms);
        return allFilms;
    }

    @Override
    public void addLike(int filmId, int userId) {
        userStorage.getUserById(userId);
        getFilmById(filmId);
        jdbcTemplate.update(SqlQueries.ADD_LIKE, filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        userStorage.getUserById(userId);
        getFilmById(filmId);
        jdbcTemplate.update(SqlQueries.DELETE_LIKE, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilmsByGenreIdAndYear(int count, Integer genreId, Integer year) {
        List<Film> popularFilms = new ArrayList<>();
        if(Objects.isNull(genreId) & Objects.isNull(year)) {
            popularFilms = jdbcTemplate.query(SqlQueries.GET_POPULAR_FILMS, new FilmMapper(), count);
        } else if (!Objects.isNull(genreId) & !Objects.isNull(year)) {
            popularFilms = jdbcTemplate.query(SqlQueries.GET_POPULAR_FILMS_BY_GENRE_ID_AND_YEAR, new FilmMapper(),
                    genreId, year, count);
        } else if (!Objects.isNull(genreId) & Objects.isNull(year)) {
            popularFilms = jdbcTemplate.query(SqlQueries.GET_POPULAR_FILMS_BY_GENRE_ID, new FilmMapper(),
                    genreId, count);
        } else {
            popularFilms = jdbcTemplate.query(SqlQueries.GET_POPULAR_FILMS_BY_YEAR, new FilmMapper(),
                    year, count);
        }
        getRatings(popularFilms);
        getGenres(popularFilms);
        return popularFilms;
    }

    @Override
    public List<Film> findFilmsByTitleOrDirector(String query, String by) {
        List<Film> foundFilms = Collections.emptyList();
        switch (by) {
            case "TITLE":
                foundFilms = jdbcTemplate.query(SqlQueries.FIND_FILMS_BY_NAME, new FilmMapper(), "%" + query + "%");
                getRatings(foundFilms);
                getGenres(foundFilms);
                break;
            case "DIRECTOR":
                foundFilms = jdbcTemplate.query(SqlQueries.FIND_FILMS_BY_DIRECTOR, new FilmMapper(), "%" + query + "%");
                getRatings(foundFilms);
                getGenres(foundFilms);
                break;
        }
        return foundFilms;
    }

    public List<Film> findAllFilmsByDirectorSortedByYearOrLikes(int directorId, String sortBy) {
        String sqlQuery;
        switch (sortBy) {
            case "YEAR":
                sqlQuery = SqlQueries.FIND_ALL_FILMS_BY_DIRECTOR_SORTED_BY_YEAR;
                break;
            case "LIKES":
            default:
                sqlQuery = SqlQueries.FIND_ALL_FILMS_BY_DIRECTOR_SORTED_BY_LIKES;
        }
        List<Film> foundFilms = jdbcTemplate.query(sqlQuery, new FilmMapper(), directorId);
        if (foundFilms.isEmpty()) {
            log.warn("Films with director id \"{}\" not found", directorId);
            throw new DirectorNotFoundException(String.format("Films with director id \"%d\" not found", directorId));
        }
        getRatings(foundFilms);
        getGenres(foundFilms);
        getDirectors(foundFilms);

        return foundFilms;
    }

    @Override
    public List<Film> getFilmsLikedByUser(int userId) {
        List<Film> filmsLikedByUser = jdbcTemplate.query(SqlQueries.GET_USERS_LIKES + " WHERE au.USER_ID = ?", new FilmMapper(), userId);
        getRatings(filmsLikedByUser);
        getGenres(filmsLikedByUser);
        getDirectors(filmsLikedByUser);
        return filmsLikedByUser;
    }

    private void updateFilmDirectors(Film film) {
        film.getDirectors().forEach(director ->
                jdbcTemplate.update(SqlQueries.ADD_FILMS_DIRECTORS, film.getId(), director.getId()));
    }

    protected void getGenres(List<Film> films) {
        SqlRowSet filmGenreIdRows = jdbcTemplate.queryForRowSet(SqlQueries.GET_GENRES_FOR_ALL_FILMS);

        while (filmGenreIdRows.next()) {
            int filmId = filmGenreIdRows.getInt("FILM_ID");
            int genreId = filmGenreIdRows.getInt("GENRE_ID");
            String genreName = filmGenreIdRows.getString("GENRE_NAME");

            for (Film film : films) {
                if (film.getId() == filmId) {
                    List<Genre> genres = film.getGenres();
                    if (genres == null) genres = new ArrayList<>();
                    Genre filmGenre = Genre.builder().id(genreId).name(genreName).build();
                    genres.add(filmGenre);
                    film.setGenres(genres);
                }
            }
        }

        for (Film film : films) {
            if (film.getGenres() == null) film.setGenres(new ArrayList<>());
        }
    }

    private void getRatings(List<Film> films) {
        SqlRowSet ratingIdRows = jdbcTemplate.queryForRowSet(SqlQueries.GET_RATINGS_FOR_ALL_FILMS);

        while (ratingIdRows.next()) {
            int filmId = ratingIdRows.getInt("FILM_ID");
            int ratingId = ratingIdRows.getInt("RATING_ID");
            String ratingName = ratingIdRows.getString("RATING_NAME");

            for (Film film : films) {
                if (film.getId() == filmId) {
                    Rating filmRating = Rating.builder().id(ratingId).name(ratingName).build();
                    if (filmRating != null) film.setMpa(filmRating);
                    break;
                }
            }
        }
    }

    private void getDirectors(List<Film> films) {
        SqlRowSet filmDirectorIdRows = jdbcTemplate.queryForRowSet(SqlQueries.GET_DIRECTORS_FOR_ALL_FILMS);

        while (filmDirectorIdRows.next()) {
            int filmId = filmDirectorIdRows.getInt("FILM_ID");
            int directorId = filmDirectorIdRows.getInt("DIRECTOR_ID");
            String directorName = filmDirectorIdRows.getString("DIRECTOR_NAME");

            for (Film film : films) {
                if (film.getId() == filmId) {
                    film.getDirectors().add(Director.builder().id(directorId).name(directorName).build());
                }
            }
        }
    }

    private List<Genre> getFilmGenres(int filmId) {
        return jdbcTemplate.query(SqlQueries.GET_FILM_GENRES, new GenreMapper(), filmId);
    }

    private Rating getFilmRating(int filmId) {
        return jdbcTemplate.query(SqlQueries.GET_FILM_RATING, new RatingMapper(), filmId).stream().findAny().orElse(null);
    }

    private Collection<Director> getFilmDirectors(int filmId) {
        return jdbcTemplate.query(SqlQueries.GET_FILM_DIRECTORS, new DirectorMapper(), filmId);
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
        jdbcTemplate.update("DELETE FROM LIKES");
        jdbcTemplate.update("DELETE FROM FRIENDS");
    }
}