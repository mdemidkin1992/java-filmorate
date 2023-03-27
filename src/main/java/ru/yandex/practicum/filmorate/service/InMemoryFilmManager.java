package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class InMemoryFilmManager implements FilmManager {
    private static int id = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film createFilm(Film film) {
        if (films.containsKey(film.getId())) {
            log.error("Film with id {} already exists", film.getId());
            throw new ValidationException("Film with id " + film.getId() + " already exists");
        }
        film.setId(++id);
        films.put(id, film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Film with id {} doesn't exist", film.getId());
            throw new ValidationException("Film with id " + film.getId() + " doesn't exist");
        }
        films.put(film.getId(), film);
        return film;
    }
}
