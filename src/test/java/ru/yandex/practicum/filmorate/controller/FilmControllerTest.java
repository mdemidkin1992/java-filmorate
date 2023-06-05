package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.db.FilmDbStorage;
import util.SimpleCrudOperations;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    private static final Validator VALIDATOR;

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
    public void shouldDeleteFilmById() throws Exception {
        Film film = createFilm(nextFilm(1999));
        deleteFilmById(film.getId());

        mockMvc.perform(get("/films/{id}", film.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof FilmNotFoundException));
    }

    @Test
    public void shouldGetAllFilmsByDirectorSortedByYearOrScores() throws Exception {
        Director director = Director.builder().name("Quentin Tarantino").build();

        MvcResult result = mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Director createdDirector = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                Director.class);

        Film film1 = nextFilm(1999);
        Film film2 = nextFilm(2000);

        film1.setDirectors(Set.of(createdDirector));
        film2.setDirectors(Set.of(createdDirector));

        film1 = createFilm(film1);
        film2 = createFilm(film2);

        List<Film> expectedFilms = new ArrayList<>();
        expectedFilms.add(film1);
        expectedFilms.add(film2);

        String sortBy = "year";

        List<Film> actualFilms = getAllFilmsByDirectorSortedByYearOrScores(createdDirector.getId(), sortBy);

        assertEquals(expectedFilms, actualFilms);
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
    public void shouldGetPopularFilms() throws Exception {
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

        final List<Film> actualPopularFilms = getPopularFilms(count);
        assertEquals(expectedPopularFilms.size(), actualPopularFilms.size());

        deleteScore(filmId2, userId3);
        deleteScore(filmId2, userId4);
        deleteScore(filmId2, userId5);

        expectedPopularFilms.clear();
        expectedPopularFilms.add(getFilmById(filmId1));
        expectedPopularFilms.add(getFilmById(filmId2));
        assertEquals(expectedPopularFilms, getPopularFilms(count));
    }

    @Test
    public void shouldUpdateFilmNormal() throws Exception {
        Film film = createFilm(nextFilm(1999));
        Film updatedFilm = getFilmById(film.getId());
        int newDuration = 201;
        updatedFilm.setDuration(newDuration);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFilm)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedFilm.getId()))
                .andExpect(jsonPath("$.name").value(updatedFilm.getName()))
                .andExpect(jsonPath("$.duration").value(newDuration))
                .andReturn();
    }

    @Test
    public void shouldNotUpdateFilmWhenIdIncorrect() throws Exception {
        Film updatedFilm = nextFilm(1999);
        updatedFilm.setId(999);

        mockMvc.perform(put("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedFilm)))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof FilmNotFoundException));
    }

    @Test
    public void shouldGetFilmsByDirectorIdSortByYear() throws Exception {
        Film film1 = nextFilm(1999);
        Film film2 = nextFilm(2000);

        String film1Name = "New movie";
        String film2Name = "Some film";

        film1.setName(film1Name);
        film2.setName(film2Name);

        film1 = createFilm(film1);
        film2 = createFilm(film2);

        String query = "some";
        String by = "year";

        List<Film> expectedFilms = new ArrayList<>();
        expectedFilms.add(film2);

        List<Film> actualFilms = searchByTitleOrDirector(query, by);

        assertEquals(expectedFilms, actualFilms);
    }

    @Test
    public void shouldNotGetFilmWhenIdIsIncorrect() throws Exception {
        int filmId = 111;

        mockMvc.perform(get("/films/{id}", filmId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof FilmNotFoundException));
    }


    @Test
    public void shouldNotAddScoreWhenUserIdIsIncorrect() throws Exception {
        Film film = createFilm(nextFilm(1999));
        int filmId = film.getId();
        int userId = 999;
        int score = 5;

        mockMvc.perform(put("/films/{id}/score/{userId}", filmId, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("score", String.valueOf(score)))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof UserNotFoundException));
    }

    @Test
    public void shouldNotAddScoreWhenFilmIdIsIncorrect() throws Exception {
        int filmId = 999;
        int score = 5;
        User user = createUser(nextUser("mark@email.com"));
        int userId = user.getId();

        mockMvc.perform(put("/films/{id}/score/{userId}", filmId, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("score", String.valueOf(score)))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof FilmNotFoundException));
    }
}