package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class InMemoryFilmManager implements FilmManager {
    private static int id = 0;
    private final Map<Integer, Film> films = new HashMap<>();
    private static final int MIN_DESCRIPTION_LENGTH = 200;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film createFilm(Film film) {
        validateFilm(film);
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
        validateFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Film name can't be empty");
            throw new ValidationException("Film name can't be empty");
        }
        if (film.getDescription().length() > 200) {
            log.error("Max film description length {}", MIN_DESCRIPTION_LENGTH);
            throw new ValidationException("Max film description length " + MIN_DESCRIPTION_LENGTH);
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.error("Min film release date {}", MIN_RELEASE_DATE);
            throw new ValidationException("Film release date should be after 28 December 1895");
        }
        if (film.getDuration() <= 0) {
            log.error("Film duration should be > 0");
            throw new ValidationException("Film duration should be > 0");
        }
    }
}
