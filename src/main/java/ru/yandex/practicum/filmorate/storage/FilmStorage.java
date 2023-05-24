package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;

public interface FilmStorage {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById(int filmId);

    List<Film> getFilms();

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    //List<Film> getPopularFilms(int count);

    List<Film> getPopularFilmsByGenreIdAndYear(int count, Integer genreId, Integer year);

    List<Film> findFilmsByTitleOrDirector(String query, String by);

    List<Film> findAllFilmsByDirectorSortedByYearOrLikes(int directorId, String sortBy);

    List<Film> getFilmsLikedByUser(int userId);
}
