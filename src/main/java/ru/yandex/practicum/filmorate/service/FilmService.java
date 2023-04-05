package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, UserStorage inMemoryUserStorage) {
        this.userStorage = inMemoryUserStorage;
        this.filmStorage = inMemoryFilmStorage;
    }

    public Film createFilm(Film film) {
        return this.filmStorage.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return this.filmStorage.updateFilm(film);
    }

    public Film getFilmById(int filmId) {
        return this.filmStorage.getFilmById(filmId);
    }

    public List<Film> getFilms() {
        return new ArrayList<>(this.filmStorage.getFilms().values());
    }

    public Set<Long> getLikes(int filmId) {
        return this.filmStorage.getFilmById(filmId).getLikes();
    }

    public Set<Long> addLike(int filmId, int userId) {
        checkFilmAndUserIds(filmId, userId);
        filmStorage.getFilmById(filmId).addLike(userId);
        return filmStorage.getFilmById(filmId).getLikes();
    }

    public Set<Long> deleteLike(int filmId, int userId) {
        checkFilmAndUserIds(filmId, userId);
        filmStorage.getFilmById(filmId).deleteLike(userId);
        return filmStorage.getFilmById(filmId).getLikes();
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getFilms().values().stream()
                .sorted((film1, film2) -> compare(film1, film2))
                .limit(count)
                .collect(Collectors.toList());
    }

    private int compare(Film film1, Film film2) {
        return Integer.compare(film2.getLikes().size(), film1.getLikes().size());
    }

    private void checkFilmAndUserIds(int filmId, int userId) {
        if (!filmStorage.getFilms().containsKey(filmId)) {
            log.error("Film with id {} doesn't exist.", filmId);
            throw new FilmNotFoundException(String.format("Film with id \"%s\" doesn't exist.", filmId));
        }
        if (!userStorage.getUsers().containsKey(userId)) {
            log.error("User with id {} doesn't exist.", userId);
            throw new UserNotFoundException(String.format("User with id \"%s\" doesn't exist.", userId));
        }
    }
}