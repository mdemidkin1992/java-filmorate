package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.db.UserDbStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private static final Validator VALIDATOR;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = validatorFactory.usingContext().getValidator();
    }

    @Test
    public void testValidations() {
        Film film1 = Film.builder().name("Titanic").description("Nothing on Earth can separate them").releaseDate(LocalDate.of(1997, 11, 1)).duration(194).mpa(Rating.builder().id(3).build()).build();
        filmDbStorage.createFilm(film1);
        int filmId = film1.getId();
        Film invalidFilm = filmDbStorage.getFilmById(filmId);
        invalidFilm.setReleaseDate(LocalDate.of(800, 1, 1));
        invalidFilm.setDuration(-5);
        Set<ConstraintViolation<Film>> validates = VALIDATOR.validate(invalidFilm);
        assertTrue(validates.size() > 0);
        validates.stream().map(v -> v.getMessage()).forEach(System.out::println);
    }

    @Test
    public void shouldGetAllFilms() {
        Film film1 = Film.builder().name("Titanic").description("Nothing on Earth can separate them").releaseDate(LocalDate.of(1997, 11, 1)).duration(194).mpa(Rating.builder().id(3).build()).build();
        Film film2 = Film.builder().name("Avatar").description("This is the new world").releaseDate(LocalDate.of(2009, 12, 17)).duration(162).mpa(Rating.builder().id(3).build()).build();

        filmDbStorage.createFilm(film1);
        filmDbStorage.createFilm(film2);

        int filmId1 = film1.getId(), filmId2 = film2.getId();
        List<Film> expected = new ArrayList<>();
        expected.add(filmDbStorage.getFilmById(filmId1));
        expected.add(filmDbStorage.getFilmById(filmId2));

        List<Film> actual = filmDbStorage.getFilms();
        assertEquals(expected, actual, "Not all films were added to storage.");
    }

    @Test
    public void shouldUpdateFilmNormal() {
        Film film1 = Film.builder().name("Titanic").description("Nothing on Earth can separate them").releaseDate(LocalDate.of(1997, 11, 1)).duration(194).mpa(Rating.builder().id(3).build()).build();
        filmDbStorage.createFilm(film1);
        int filmId = film1.getId();
        Film originalFilm = filmDbStorage.getFilmById(filmId);
        Film updatedFilm = originalFilm;
        updatedFilm.setDuration(201);
        filmDbStorage.updateFilm(updatedFilm);

        assertEquals(updatedFilm, filmDbStorage.getFilmById(updatedFilm.getId()));
        filmDbStorage.updateFilm(originalFilm);
    }

    @Test
    public void shouldNotUpdateFilmWhenIdIncorrect() {
        Film updatedFilm = Film.builder().id(999).build();
        FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () -> filmDbStorage.updateFilm(updatedFilm));
        String expectedMessage = "Film with id " + updatedFilm.getId() + " doesn't exist";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldAddLikeFromUserWithCorrectId() {
        Film film1 = Film.builder().name("Titanic").description("Nothing on Earth can separate them").releaseDate(LocalDate.of(1997, 11, 1)).duration(194).mpa(Rating.builder().id(3).build()).build();
        Film film2 = Film.builder().name("Avatar").description("This is the new world").releaseDate(LocalDate.of(2009, 12, 17)).duration(162).mpa(Rating.builder().id(3).build()).build();

        filmDbStorage.createFilm(film1);
        filmDbStorage.createFilm(film2);

        int filmId1 = film1.getId(), filmId2 = film2.getId();

        User user1 = User.builder().name("Mark").login("marklogin").email("mark@email.com").birthday(LocalDate.of(1992, 1, 2)).build();
        User user2 = User.builder().name("Ben").login("benlogin").email("ben@email.com").birthday(LocalDate.of(1995, 2, 4)).build();
        User user3 = User.builder().name("Clark").login("clarklogin").email("clark@email.com").birthday(LocalDate.of(1997, 4, 6)).build();
        User user4 = User.builder().name("Ben").login("benlogin").email("ben@email.com").birthday(LocalDate.of(2000, 6, 10)).build();
        User user5 = User.builder().name("Ben").login("benlogin").email("ben@email.com").birthday(LocalDate.of(2001, 8, 12)).build();

        userDbStorage.createUser(user1);
        userDbStorage.createUser(user2);
        userDbStorage.createUser(user3);
        userDbStorage.createUser(user4);
        userDbStorage.createUser(user5);

        int userId1 = user1.getId(), userId2 = user2.getId(), userId3 = user3.getId(), userId4 = user4.getId(), userId5 = user5.getId();

        filmDbStorage.addLike(filmId1, userId1);
        filmDbStorage.addLike(filmId1, userId2);
        filmDbStorage.addLike(filmId2, userId3);
        filmDbStorage.addLike(filmId2, userId4);
        filmDbStorage.addLike(filmId2, userId5);

        final int count = 2;
        final List<Film> expectedPopularFilms = new ArrayList<>();
        expectedPopularFilms.add(filmDbStorage.getFilmById(filmId1));
        expectedPopularFilms.add(filmDbStorage.getFilmById(filmId1));

        final List<Film> actualPopularFilms = filmDbStorage.getPopularFilms(count);
        assertEquals(expectedPopularFilms.size(), actualPopularFilms.size());

        filmDbStorage.deleteLike(filmId1, userId1);
        filmDbStorage.deleteLike(filmId1, userId2);

        expectedPopularFilms.clear();
        expectedPopularFilms.add(filmDbStorage.getFilmById(filmId2));
        expectedPopularFilms.add(filmDbStorage.getFilmById(filmId1));
        assertEquals(expectedPopularFilms, filmDbStorage.getPopularFilms(count));
    }

    @Test
    public void shouldNotGetFilmWhenIdIsIncorrect() {
        int filmId = 999;
        FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () -> filmDbStorage.getFilmById(filmId));
        String expectedMessage = "Film with id " + filmId + " doesn't exist";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotAddLikeWhenUserIdIsIncorrect() {
        Film film1 = Film.builder().name("Titanic").description("Nothing on Earth can separate them").releaseDate(LocalDate.of(1997, 11, 1)).duration(194).mpa(Rating.builder().id(3).build()).build();
        filmDbStorage.createFilm(film1);
        int filmId1 = film1.getId();
        int userId = 999;
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> filmDbStorage.addLike(filmId1, userId));
        String expectedMessage = "User with id " + userId + " doesn't exist";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotAddLikeWhenFilmIdIsIncorrect() {
        int filmId = 999;
        User user1 = User.builder().name("Mark").login("marklogin").email("mark@email.com").birthday(LocalDate.of(1992, 1, 2)).build();
        userDbStorage.createUser(user1);
        int userId1 = user1.getId();

        FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () -> filmDbStorage.addLike(filmId, userId1));
        String expectedMessage = "Film with id " + filmId + " doesn't exist";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @AfterEach
    public void clearDb() {
        filmDbStorage.clearDb();
    }
}