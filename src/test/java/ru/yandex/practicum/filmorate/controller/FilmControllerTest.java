package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.impl.mem.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.mem.InMemoryUserStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private static FilmController filmController;
    private static Film film1, film2, film3, film4, film5;
    private static User user1, user2, user3, user4, user5;
    private static final Validator VALIDATOR;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = validatorFactory.usingContext().getValidator();
    }

    @BeforeAll
    public static void beforeAll() {
        InMemoryUserStorage userStorage = new InMemoryUserStorage();
        InMemoryFilmStorage filmStorage = new InMemoryFilmStorage();
        UserController userController = new UserController(new UserService(userStorage));
        filmController = new FilmController(new FilmService(filmStorage, userStorage));

        film1 = Film.builder().name("Film name 1").description("Film description 1").releaseDate(LocalDate.of(1990, 1, 1)).duration(90).build();
        film2 = Film.builder().name("Film name 2").description("Film description 2").releaseDate(LocalDate.of(1991, 1, 1)).duration(90).build();
        film3 = Film.builder().name("Film name 3").description("Film description 3").releaseDate(LocalDate.of(1992, 1, 1)).duration(90).build();
        film4 = Film.builder().name("Film name 4").description("Film description 4").releaseDate(LocalDate.of(1993, 1, 1)).duration(90).build();
        film5 = Film.builder().name("Film name 5").description("Film description 5").releaseDate(LocalDate.of(1994, 1, 1)).duration(90).build();

        user1 = User.builder().id(1).email("example_1@email.ru").login("login_1").name("name_1").birthday(LocalDate.of(1990, 1, 10)).build();
        user2 = User.builder().id(2).email("example_2@email.ru").login("login_2").name("name_2").birthday(LocalDate.of(1991, 2, 11)).build();
        user3 = User.builder().id(3).email("example_3@email.ru").login("login_3").name("name_3").birthday(LocalDate.of(1992, 3, 12)).build();
        user4 = User.builder().id(4).email("example_4@email.ru").login("login_4").name("name_4").birthday(LocalDate.of(1993, 4, 13)).build();
        user5 = User.builder().id(5).email("example_5@email.ru").login("login_5").name("name_5").birthday(LocalDate.of(1994, 5, 14)).build();


        filmController.createFilm(film1);
        filmController.createFilm(film2);
        filmController.createFilm(film3);
        filmController.createFilm(film4);
        filmController.createFilm(film5);

        userController.createUser(user1);
        userController.createUser(user2);
        userController.createUser(user3);
        userController.createUser(user4);
        userController.createUser(user5);
    }

    @Test
    public void testValidations() {
        Film invalidFilm = Film.builder().id(1).name(null).description(null).releaseDate(LocalDate.of(800, 1, 1)).duration(-5).build();
        Set<ConstraintViolation<Film>> validates = VALIDATOR.validate(invalidFilm);
        assertTrue(validates.size() > 0);
        validates.stream().map(v -> v.getMessage()).forEach(System.out::println);
        filmController.updateFilm(film1);
    }

    @Test
    public void shouldGetAllFilms() {
        List<Film> expected = new ArrayList<>();
        expected.add(film1);
        expected.add(film2);
        expected.add(film3);
        expected.add(film4);
        expected.add(film5);

        List<Film> actual = filmController.getFilms();
        assertEquals(expected, actual, "Not all users were added to storage.");
    }

    @Test
    public void shouldNotAddFilmWhenFilmAlreadyExists() {
        ValidationException exception = assertThrows(ValidationException.class, () -> filmController.createFilm(film1));
        String expectedMessage = "Film with id " + film1.getId() + " already exists";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldUpdateFilmNormal() {
        Film updatedFilm = Film.builder().id(1).build();
        filmController.updateFilm(updatedFilm);

        assertEquals(updatedFilm, filmController.getFilmById(updatedFilm.getId()));
        filmController.updateFilm(film1);
    }

    @Test
    public void shouldNotUpdateFilmWhenIdIncorrect() {
        Film updatedFilm = Film.builder().id(999).build();

        FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () -> filmController.updateFilm(updatedFilm));
        String expectedMessage = "Film with id " + updatedFilm.getId() + " doesn't exist";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldAddLikeFromUserWithCorrectId() {
        final int film1Id = film1.getId();
        final int film2Id = film2.getId();
        final int user1Id = user1.getId();
        final int user2Id = user2.getId();
        final int user3Id = user3.getId();
        final int user4Id = user4.getId();
        final int user5Id = user5.getId();

        filmController.addLike(film1Id, user1Id);
        filmController.addLike(film1Id, user2Id);
        filmController.addLike(film1Id, user3Id);
        filmController.addLike(film2Id, user4Id);
        filmController.addLike(film2Id, user5Id);

        final Set<Long> expectedLikesFirstFilm = new HashSet<>();
        expectedLikesFirstFilm.add((long) user1Id);
        expectedLikesFirstFilm.add((long) user2Id);
        expectedLikesFirstFilm.add((long) user3Id);

        final Set<Long> expectedLikesSecondFilm = new HashSet<>();
        expectedLikesSecondFilm.add((long) user4Id);
        expectedLikesSecondFilm.add((long) user5Id);

        final Set<Long> actualLikesFirstFilm = filmController.getFilms().get(film1.getId() - 1).getLikes();
        final Set<Long> actualLikesSecondFilm = filmController.getFilms().get(film2.getId() - 1).getLikes();

        assertEquals(expectedLikesFirstFilm, actualLikesFirstFilm);
        assertEquals(expectedLikesSecondFilm, actualLikesSecondFilm);

        final int count = 2;
        final List<Film> expectedPopularFilms = new ArrayList<>();
        expectedPopularFilms.add(film1);
        expectedPopularFilms.add(film2);

        final List<Film> actualPopularFilms = filmController.getPopularFilms(count);
        assertEquals(expectedPopularFilms, actualPopularFilms);

        filmController.deleteLike(film1Id, user1Id);
        expectedLikesFirstFilm.remove((long) user1Id);
        assertEquals(expectedLikesFirstFilm, filmController.getFilmById(film1.getId()).getLikes());
    }

    @Test
    public void shouldNotGetFilmWhenIdIsIncorrect() {
        int filmId = 999;
        FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () -> filmController.getFilmById(filmId));
        String expectedMessage = "Film with id " + filmId + " doesn't exist";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotAddLikeWhenUserIdIsIncorrect() {
        int userId = 999;
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> filmController.addLike(film1.getId(), userId));
        String expectedMessage = "User with id \"" + userId + "\" doesn't exist.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotAddLikeWhenFilmIdIsIncorrect() {
        int filmId = 999;
        FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () -> filmController.addLike(filmId, user1.getId()));
        String expectedMessage = "Film with id \"" + filmId + "\" doesn't exist.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}