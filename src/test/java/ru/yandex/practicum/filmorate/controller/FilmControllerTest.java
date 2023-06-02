package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.db.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.db.UserDbStorage;
import util.SimpleCrudOperations;

import javax.print.attribute.standard.Media;
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

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.CustomEasyRandom.nextFilm;
import static util.CustomEasyRandom.nextUser;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmControllerTest extends SimpleCrudOperations {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final DirectorDbStorage directorDbStorage;
    private static final Validator VALIDATOR;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = validatorFactory.usingContext().getValidator();
    }

    @AfterEach
    public void clearDb() {
        filmDbStorage.clearTableAndResetIds();
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
    public void shouldGetAllFilms() throws Exception {
        createFilm(nextFilm(2000));
        createFilm(nextFilm(2001));
        mockMvc.perform(get("/films").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andReturn();
    }

    @Test
    public void shouldGetCommonFilms() throws Exception {
        Film film1 = createFilm(nextFilm(1999));
        Film film2 = createFilm(nextFilm(2000));

        int filmId1 = film1.getId();
        int filmId2 = film2.getId();

        User user1 = createUser(nextUser("mark@email.com"));
        User user2 = createUser(nextUser("ben@email.com"));

        int userId1 = user1.getId();
        int userId2 = user2.getId();

        int likeScore1 = 5;

        addScore(filmId1, userId1, likeScore1);
        addScore(filmId1, userId2, likeScore1);
        addScore(filmId2, userId2, likeScore1);

        List<Film> expectedCommonFilms = new ArrayList<>();
        expectedCommonFilms.add(getFilmById(filmId1));

        List<Film> actualCommonFilms = getCommonFilms(userId1, userId2);

        assertEquals(expectedCommonFilms, actualCommonFilms);
    }

    @Test
    public void shouldAddScoresFromUserWithCorrectId() throws Exception {
        Film film1 = createFilm(nextFilm(1999));
        Film film2 = createFilm(nextFilm(2000));

        int filmId1 = film1.getId();
        int filmId2 = film2.getId();

        User user1 = createUser(nextUser("mark@email.com"));
        User user2 = createUser(nextUser("ben@email.com"));
        User user3 = createUser(nextUser("tom@email.com"));
        User user4 = createUser(nextUser("bob@email.com"));
        User user5 = createUser(nextUser("kirk@email.com"));

        int userId1 = user1.getId();
        int userId2 = user2.getId();
        int userId3 = user3.getId();
        int userId4 = user4.getId();
        int userId5 = user5.getId();

        int likeScore1 = 5;
        int likeScore2 = 8;

        addScore(filmId1, userId1, likeScore1);
        addScore(filmId1, userId2, likeScore1);
        addScore(filmId2, userId3, likeScore2);
        addScore(filmId2, userId4, likeScore2);
        addScore(filmId2, userId5, likeScore2);

        final int count = 2;
        final List<Film> expectedPopularFilms = new ArrayList<>();
        expectedPopularFilms.add(getFilmById(filmId2));
        expectedPopularFilms.add(getFilmById(filmId1));

        final List<Film> actualPopularFilms = filmDbStorage.getPopularFilmsByGenreIdAndYear(count, null, null);
        assertEquals(expectedPopularFilms.size(), actualPopularFilms.size());

        deleteScore(filmId1, userId1);
        deleteScore(filmId1, userId2);

        expectedPopularFilms.clear();
        expectedPopularFilms.add(getFilmById(filmId2));
        expectedPopularFilms.add(getFilmById(filmId1));
        assertEquals(expectedPopularFilms, filmDbStorage.getPopularFilmsByGenreIdAndYear(count, null, null));
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
    public void shouldGetFilmsByDirectorIdSortByYear() {
        Director director = Director.builder().name("Director").build();
        directorDbStorage.createDirector(director);
        Set<Director> directors = new HashSet<>();
        directors.add(director);

        Film film1 = Film.builder().name("Titanic").description("Nothing on Earth can separate them").releaseDate(LocalDate.of(1997, 11, 1)).duration(194).mpa(Rating.builder().id(3).name("PG-13").build()).directors(directors).build();
        Film film2 = Film.builder().name("Avatar").description("This is the new world").releaseDate(LocalDate.of(2009, 12, 17)).duration(162).mpa(Rating.builder().id(3).name("PG-13").build()).directors(directors).build();

        filmDbStorage.createFilm(film2);
        filmDbStorage.createFilm(film1);

        List<Film> actual = filmDbStorage.findAllFilmsByDirectorSortedByYearOrScores(director.getId(), "YEAR");
        List<Film> expected = new ArrayList<>();
        expected.add(film1);
        expected.add(film2);
        System.out.println(actual);
        assertEquals(expected, actual);
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
    public void shouldNotAddScoreWhenUserIdIsIncorrect() {
        Film film1 = Film.builder().name("Titanic").description("Nothing on Earth can separate them").releaseDate(LocalDate.of(1997, 11, 1)).duration(194).mpa(Rating.builder().id(3).build()).build();
        filmDbStorage.createFilm(film1);
        int filmId1 = film1.getId();
        int userId = 999;
        int likeScore = 5;
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> filmDbStorage.addScore(filmId1, userId, likeScore));
        String expectedMessage = "User with id " + userId + " doesn't exist";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotAddScoreWhenFilmIdIsIncorrect() {
        int filmId = 999;
        int likeScore = 5;
        User user1 = User.builder().name("Mark").login("marklogin").email("mark@email.com").birthday(LocalDate.of(1992, 1, 2)).build();
        userDbStorage.createUser(user1);
        int userId1 = user1.getId();

        FilmNotFoundException exception = assertThrows(FilmNotFoundException.class, () -> filmDbStorage.addScore(filmId, userId1, likeScore));
        String expectedMessage = "Film with id " + filmId + " doesn't exist";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}