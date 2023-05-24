package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.aspects.annotation.SaveUserEvent;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenresStorage;
import ru.yandex.practicum.filmorate.storage.RatingStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;
    private final GenresStorage genresStorage;
    private final RatingStorage ratingStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, GenresStorage genresStorage, RatingStorage ratingStorage) {
        this.filmStorage = filmStorage;
        this.genresStorage = genresStorage;
        this.ratingStorage = ratingStorage;
    }

    public Film createFilm(Film film) {
        return filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmStorage.updateFilm(film);
    }

    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    @SaveUserEvent(eventType = EventType.LIKE, operation = OperationType.ADD, entityIdParamName = "filmId")
    public void addLike(int filmId, int userId) {
        filmStorage.addLike(filmId, userId);
    }

    @SaveUserEvent(eventType = EventType.LIKE, operation = OperationType.REMOVE, entityIdParamName = "filmId")
    public void deleteLike(int filmId, int userId) {
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public List<Rating> getRatings() {
        return ratingStorage.getAllRatings();
    }

    public Rating getRatingById(int ratingId) {
        return ratingStorage.getRatingById(ratingId);
    }

    public List<Genre> getGenres() {
        return genresStorage.getAllGenres();
    }

    public Genre getGenreById(int genreId) {
        return genresStorage.getGenreById(genreId);
    }

    public List<Film> searchFilmsByTitleOrDirector(String query, String by) {
        String formattedBy = by.toUpperCase().replaceAll("\\s", "");
        switch (formattedBy) {
            case "TITLE":
                return filmStorage.findFilmsByTitle(query.toLowerCase());
            case "DIRECTOR":
                return filmStorage.findFilmsByDirector(query.toLowerCase());
            default:
                return filmStorage.findFilmsByTitleOrDirector(query.toLowerCase());
        }
    }

    public List<Film> getAllFilmsByDirectorSortedByYearOrLikes(int directorId, String sortBy) {
        return filmStorage.findAllFilmsByDirectorSortedByYearOrLikes(directorId, sortBy.toUpperCase());
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        List<Film> userFilms = filmStorage.getFilmsLikedByUser(userId);
        List<Film> friendFilms = filmStorage.getFilmsLikedByUser(friendId);

        Set<Film> intersection = new HashSet<>(userFilms);
        intersection.retainAll(friendFilms);

        return new ArrayList<>(intersection);
    }
}