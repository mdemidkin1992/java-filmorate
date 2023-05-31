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

    void addScore(int filmId, int userId, int score);

    void deleteScore(int filmId, int userId);

    List<Film> getPopularFilmsByGenreIdAndYear(int count, Integer genreId, Integer year);

    List<Film> findFilmsByTitle(String query);

    List<Film> findFilmsByDirector(String query);

    List<Film> findFilmsByTitleOrDirector(String query);

    List<Film> findAllFilmsByDirectorSortedByYearOrScores(int directorId, String sortBy);

    List<Film> getFilmsScoredByUser(int userId);

    void getFilmScoresStats(Map<Integer, HashMap<Integer, Double>> inputData, List<Integer> allFilmsIds);

    void deleteFilmById(int filmId);
}
