package ru.yandex.practicum.filmoweb.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmoweb.exception.ValidationException;
import ru.yandex.practicum.filmoweb.model.Film;
import ru.yandex.practicum.filmoweb.service.InMemoryFilmManager;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {
    private static Film film;
    private static FilmController filmController;

    @BeforeEach
    public void beforeEach() {
        filmController = new FilmController(new InMemoryFilmManager());
        film = Film.builder()
                .name("Film name")
                .description("Film description")
                .releaseDate(LocalDate.of(1990, 1, 1))
                .duration(90)
                .build();
    }

    @Test
    public void shouldAddFilm() {
        filmController.createFilm(film);
        List<Film> expectedFilms = List.of(film);
        List<Film> savedFilms = filmController.findFilms();
        assertEquals(expectedFilms, savedFilms);
        assertEquals(1, filmController.findFilms().size());
    }

    @Test
    public void shouldNotAddFilmWhenNameEmpty() {
        film.setName(" ");
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        String expectedMessage = "Film name can't be empty";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotAddFilmWhenReleaseDateIsIncorrect() {
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        String expectedMessage = "Film release date should be after 28 December 1895";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotAddFilmWhenDescriptionLengthMoreThan200() {
        char[] chars = new char[201];
        Arrays.fill(chars, 'n');
        film.setDescription(new String(chars));
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        String expectedMessage = "Max film description length 200";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotAddFilmWhenDurationIsNegative() {
        film.setDuration(-10);
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.createFilm(film));
        String expectedMessage = "Film duration should be > 0";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldUpdateUserNormal() {
        filmController.createFilm(film);

        Film updatedFilm = film;
        updatedFilm.setName("New film name");
        updatedFilm.setDescription("New film description");
        updatedFilm.setReleaseDate(LocalDate.of(2000, 12, 12));
        updatedFilm.setDuration(100);

        filmController.updateFilm(updatedFilm);

        assertEquals(updatedFilm.toString(), filmController.findFilms().get(0).toString());
    }

    @Test
    public void shouldNotUpdateFilmWhenIdIncorrect() {
        filmController.createFilm(film);

        Film updatedFilm = film;
        updatedFilm.setId(999);
        updatedFilm.setName("New film name");
        updatedFilm.setDescription("New film description");
        updatedFilm.setReleaseDate(LocalDate.of(2000, 12, 12));
        updatedFilm.setDuration(100);

        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.updateFilm(updatedFilm));
        String expectedMessage = "Film with id " + updatedFilm.getId() + " doesn't exist";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}