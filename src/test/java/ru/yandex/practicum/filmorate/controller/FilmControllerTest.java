package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.InMemoryFilmManager;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private static Film film;
    private static FilmController filmController;
    private final static Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

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
    public void testValidations() {
        Film invalidFilm = filmController.createFilm(film);
        invalidFilm.setName(null);
        invalidFilm.setDescription(null);
        invalidFilm.setReleaseDate(LocalDate.of(800, 1, 1));
        invalidFilm.setDuration(-5);
        char[] chars = new char[201];
        Arrays.fill(chars, 'n');
        invalidFilm.setDescription(new String(chars));

        Set<ConstraintViolation<Film>> validates = validator.validate(invalidFilm);
        assertTrue(validates.size() > 0);
        validates.stream().map(v -> v.getMessage()).forEach(System.out::println);
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
    public void shouldNotAddFilmWhenFilmAlreadyExists() {
        Film newFilm = filmController.createFilm(film);
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.createFilm(newFilm));
        String expectedMessage = "Film with id " + newFilm.getId() + " already exists";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldUpdateFilmNormal() {
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