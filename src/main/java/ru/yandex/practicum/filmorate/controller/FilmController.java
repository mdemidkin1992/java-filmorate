package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmManager;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmManager manager;

    public FilmController(@Autowired FilmManager manager) {
        this.manager = manager;
    }

    @GetMapping
    public List<Film> findFilms() {
        log.info("Number of films: {}", manager.getFilms().size());
        return manager.getFilms();
    }

    @PostMapping
    public Film createFilm(@NotNull @RequestBody Film film) {
        log.info("POST request received: {}", film);
        Film response = manager.createFilm(film);
        log.info("Added film: {}", film.toString());
        return response;
    }

    @PutMapping
    public Film updateFilm(@NotNull @RequestBody Film film) {
        log.info("PUT request received: {}", film);
        Film response = manager.updateFilm(film);
        log.info("Updated film: {}", film.toString());
        return response;
    }
}
