package ru.yandex.practicum.filmorate.storage.impl.mem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component("inMemoryFilmStorage")
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private static int id = 0;
    private final Map<Integer, Film> films = new HashMap<>();
    private final InMemoryUserStorage inMemoryUserStorage;

    public InMemoryFilmStorage(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public void addLike(int filmId, int userId) {
        checkFilmAndUserIds(filmId, userId);
        getFilmById(filmId).addLike(userId);
    }

    @Override
    public void deleteLike(int filmId, int userId) {
        checkFilmAndUserIds(filmId, userId);
        getFilmById(filmId).deleteLike(userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return films.values().stream()
                .sorted((film1, film2) -> compare(film1, film2))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film getFilmById(int filmId) {
        if (!films.containsKey(filmId)) {
            log.error("Film with id {} doesn't exist", filmId);
            throw new FilmNotFoundException("Film with id " + filmId + " doesn't exist");
        }
        return this.films.get(filmId);
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
            throw new FilmNotFoundException("Film with id " + film.getId() + " doesn't exist");
        }
        films.put(film.getId(), film);
        return film;
    }
    private int compare(Film film1, Film film2) {
        return Integer.compare(film2.getLikes().size(), film1.getLikes().size());
    }

    private void checkFilmAndUserIds(int filmId, int userId) {
        if (!films.containsKey(filmId)) {
            log.error("Film with id {} doesn't exist.", filmId);
            throw new FilmNotFoundException(String.format("Film with id \"%s\" doesn't exist.", filmId));
        }
    }
}
