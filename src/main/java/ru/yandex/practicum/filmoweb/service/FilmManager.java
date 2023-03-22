package ru.yandex.practicum.filmoweb.service;

import ru.yandex.practicum.filmoweb.model.Film;

import java.util.List;

public interface FilmManager {
    Film createFilm(Film film);

    Film updateFilm(Film film);

    List<Film> getFilms();
}
