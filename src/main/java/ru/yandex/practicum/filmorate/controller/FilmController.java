package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

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
        log.info("GET request for all films received");
        List<Film> response = filmService.getFilms();
        log.info("Number of films: {}", response.size());
        log.info("All films: {}", response);
        return response;
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
    public List<Film> getPopularFilmsByGenreIdAndYear(@RequestParam(defaultValue = "10", required = false) int count,
                                                      @RequestParam(required = false) Integer genreId,
                                                      @RequestParam(required = false) Integer year) {
        log.info("GET request for count = {} genreId = {} release year = {}", count, genreId, year);
        List<Film> response = filmService.getPopularFilmsByGenreIdAndYear(count, genreId, year);
        log.info("Number of films: {}", response.size());
        log.info("Most popular films: {}", response);
        return response;
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(
            @RequestParam int userId, @RequestParam int friendId
    ) {
        log.info("GET request received: users {} and {} common films", userId, friendId);
        List<Film> response = filmService.getCommonFilms(userId, friendId);
        log.info("Common films: {}", response);
        return response;
    }

    @GetMapping("/search")
    public List<Film> searchFilmsByTitleOrDirector(
            @RequestParam String query, @RequestParam String by) {
        log.info("GET request received: search films {} by {}", query, by);
        List<Film> response = filmService.searchFilmsByTitleOrDirector(query, by);
        log.info("Found films: {}", response);
        return response;
    }

    @GetMapping("/director/{id}")
    public List<Film> getAllFilmsByDirectorSortedByYearOrLikes(
            @PathVariable("id") int directorId, @RequestParam String sortBy) {
        log.info("GET request received: get all films by director id \"{}\", sorting by \"{}\"", directorId, sortBy);
        List<Film> response = filmService.getAllFilmsByDirectorSortedByYearOrLikes(directorId, sortBy);
        log.info("Found films: {}", response);
        return response;
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable("filmId") int filmId) {
        log.info("DELETE request received: delete film by id \"{}\"", filmId);
        filmService.deleteFilmById(filmId);
        log.info("Films with id \"{}\" deleted", filmId);
    }
}