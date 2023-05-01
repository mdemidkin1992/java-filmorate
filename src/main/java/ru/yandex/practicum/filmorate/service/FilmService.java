package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
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

    public void addLike(int filmId, int userId) {
        filmStorage.addLike(filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        filmStorage.deleteLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    public List<Rating> getRatings() {
        return filmStorage.getAllRatings();
    }

    public Rating getRatingById(int ratingId) {
        return filmStorage.getRatingById(ratingId);
    }

    public List<Genre> getGenres() {
        return filmStorage.getAllGenres();
    }

    public Genre getGenreById(int genreId) {
        return filmStorage.getGenreById(genreId);
    }
}