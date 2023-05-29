package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface FilmStorage {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(int filmId);

    List<Film> getAllFilms();

    List<Film> getFilmsWhereIdEquals(List<Integer> filmsIds);

    void addLike(int filmId, int userId);

    void addScore(int filmId, int userId, int score);

    void deleteLike(int filmId, int userId);

    List<Film> getPopularFilmsByGenreIdAndYear(int count, Integer genreId, Integer year);

    List<Film> findFilmsByTitle(String query);

    List<Film> findFilmsByDirector(String query);

    List<Film> findFilmsByTitleOrDirector(String query);

    List<Film> findAllFilmsByDirectorSortedByYearOrLikes(int directorId, String sortBy);

    List<Film> getFilmsLikedByUser(int userId);

    void getFilmScoresStats(Map<Integer, HashMap<Integer, Double>> inputData, List<Integer> allFilmsIds);

    void deleteFilmById(int filmId);
}
