package ru.yandex.practicum.filmorate.service.predictor;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface Predictor {

    List<Film> recommendFilms(int userId);

}
