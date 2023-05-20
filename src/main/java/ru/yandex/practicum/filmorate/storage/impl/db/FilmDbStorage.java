package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.impl.db.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.impl.db.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.impl.db.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.utility.SqlQueries;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

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
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        checkFilmId(film.getId());
        jdbcTemplate.update(SqlQueries.UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        jdbcTemplate.update(SqlQueries.DELETE_FILMS_GENRES, film.getId());
        updateFilmGenres(film);
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

        film.setMpa(mpa);
        film.setGenres(genres);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sql = SqlQueries.GET_FILMS_WITH_GENRES;
        List<Film> allFilms = jdbcTemplate.query(sql, new FilmMapper());
        getGenres(allFilms, sql);
        getRatings(allFilms, sql);
        return allFilms;
    }

    @Override
    public void addLike(int filmId, int userId) {
        checkUserId(userId);
        checkFilmId(filmId);
        jdbcTemplate.update(SqlQueries.ADD_LIKE, filmId, userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        checkUserId(userId);
        checkFilmId(filmId);
        jdbcTemplate.update(SqlQueries.DELETE_LIKE, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = SqlQueries.GET_POPULAR_FILMS_WITH_GENRES;
        List<Film> popularFilms = jdbcTemplate.query(sql, new FilmMapper());
        getRatings(popularFilms, sql);
        getGenres(popularFilms, sql);
        return popularFilms.stream().limit(count).collect(Collectors.toList());
    }

    private void getGenres(List<Film> films, String sql) {
        SqlRowSet filmGenreIdRows = jdbcTemplate.queryForRowSet(sql);

        while (filmGenreIdRows.next()) {
            int filmId = filmGenreIdRows.getInt("FILM_ID");
            int genreId = filmGenreIdRows.getInt("GENRE_ID");
            String genreName = filmGenreIdRows.getString("GENRE_NAME");

            for (Film film : films) {
                if (film.getId() == filmId) {
                    List<Genre> genres = film.getGenres();
                    if (genres == null)
                        genres = new ArrayList<>();
                    if (genreId != 0 && genreName != null) {
                        Genre filmGenre = Genre.builder().id(genreId).name(genreName).build();
                        genres.add(filmGenre);
                    }
                    film.setGenres(genres);
                }
            }
        }

        for (Film film : films) {
            if (film.getGenres() == null) film.setGenres(new ArrayList<>());
        }
    }

    private void getRatings(List<Film> films, String sql) {
        SqlRowSet ratingIdRows = jdbcTemplate.queryForRowSet(sql);

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

    private List<Genre> getFilmGenres(int filmId) {
        return jdbcTemplate.query(SqlQueries.GET_FILM_GENRES, new GenreMapper(), filmId);
    }

    private Rating getFilmRating(int filmId) {
        return jdbcTemplate.query(SqlQueries.GET_FILM_RATING, new RatingMapper(), filmId).stream().findAny().orElse(null);
    }

    private void updateFilmGenres(Film film) {
        List<Genre> filmGenres = film.getGenres();
        if (filmGenres != null) {
            List<Genre> filmGenresWithoutDublicates = new ArrayList<>(new LinkedHashSet<>(film.getGenres()));
            film.setGenres(filmGenresWithoutDublicates);
            for (Genre genre : filmGenresWithoutDublicates) {
                jdbcTemplate.update(SqlQueries.ADD_FILMS_GENRES, film.getId(), genre.getId());
            }
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

    private void checkUserId(int userId) {
        if (userStorage.getUserById(userId) == null) {
            log.error("User with id {} doesn't exist", userId);
            throw new UserNotFoundException("User with id " + userId + " doesn't exist");
        }
    }

    private void checkFilmId(int filmId) {
        if (getFilmById(filmId) == null) {
            log.error("Film with id {} doesn't exist", filmId);
            throw new FilmNotFoundException("Film with id " + filmId + " doesn't exist");
        }
    }

    public void clearDb() {
        jdbcTemplate.update("DELETE FROM APP_USERS");
        jdbcTemplate.update("DELETE FROM FILMS");
        jdbcTemplate.update("DELETE FROM FILMS_GENRES");
        jdbcTemplate.update("DELETE FROM LIKES");
        jdbcTemplate.update("DELETE FROM FRIENDS");
    }
}