package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        log.info("Number of films: {}", filmService.getFilms().size());
        return filmService.getFilms();
    }

    @PostMapping
    public Film createFilm(@NotNull @RequestBody @Valid Film film) {
        log.info("POST request received: {}", film);
        Film response = filmService.createFilm(film);
        log.info("Added film: {}", response.toString());
        return response;
    }

    @PutMapping
    public Film updateFilm(@NotNull @RequestBody @Valid Film film) {
        log.info("PUT request received: {}", film);
        Film response = filmService.updateFilm(film);
        log.info("Updated film: {}", response.toString());
        return response;
    }

    @GetMapping("{id}")
    public Film getFilmById(@PathVariable("id") int filmId) {
        log.info("GET request received: film with id \"{}\"", filmId);
        Film response = filmService.getFilmById(filmId);
        log.info("Film with id \"{}\" : {}", filmId, response.toString());
        return response;
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable("id") int filmId,
                             @PathVariable("userId") int userId) {
        log.info("PUT request received: user id \"{}\" likes film id \"{}\"", userId, filmId);
        filmService.addLike(filmId, userId);
        log.info("Film \"{}\" added like from user \"{}\"", filmId, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") int filmId,
                                @PathVariable("userId") int userId) {
        log.info("DELETE request received: user id \"{}\" deletes like from film id \"{}\"", userId, filmId);
        filmService.deleteLike(filmId, userId);
        log.info("Film \"{}\" deleted like from user \"{}\"", filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(
            @RequestParam(defaultValue = "10", required = false) int count
    ) {
        log.info("GET request received: top-{} popular films", count);
        List<Film> response = filmService.getPopularFilms(count);
        log.info("Most popular films: {}", response);
        return response;
    }
}
