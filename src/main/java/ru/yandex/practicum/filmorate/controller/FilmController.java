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
        return filmService.getFilms();
    }

    @PostMapping
    public Film createFilm(@NotNull @RequestBody @Valid Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@NotNull @RequestBody @Valid Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping("{id}")
    public Film getFilmById(@PathVariable("id") int filmId) {
        return filmService.getFilmById(filmId);
    }

    @PutMapping("{id}/like/{userId}/{likeScore}")
    public void addLike(@PathVariable("id") int filmId,
                        @PathVariable("userId") int userId,
                        @NotNull @PathVariable ("likeScore") int likeScore) {
        filmService.addLike(filmId, userId, likeScore);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") int filmId,
                           @PathVariable("userId") int userId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilmsByGenreIdAndYear(@RequestParam(defaultValue = "10", required = false) int count,
                                                      @RequestParam(required = false) Integer genreId,
                                                      @RequestParam(required = false) Integer year) {
        return filmService.getPopularFilmsByGenreIdAndYear(count, genreId, year);
    }

    @GetMapping("/common")
    public List<Film> getCommonFilms(
            @RequestParam int userId, @RequestParam int friendId
    ) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/search")
    public List<Film> searchFilmsByTitleOrDirector(
            @RequestParam String query, @RequestParam String by) {
        return filmService.searchFilmsByTitleOrDirector(query, by);
    }

    @GetMapping("/director/{id}")
    public List<Film> getAllFilmsByDirectorSortedByYearOrLikes(
            @PathVariable("id") int directorId, @RequestParam String sortBy) {
        return filmService.getAllFilmsByDirectorSortedByYearOrLikes(directorId, sortBy);
    }

    @DeleteMapping("/{filmId}")
    public void deleteFilmById(@PathVariable("filmId") int filmId) {
        filmService.deleteFilmById(filmId);
    }
}