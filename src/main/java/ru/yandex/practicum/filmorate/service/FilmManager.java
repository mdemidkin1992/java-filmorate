package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmManager {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();
}
