package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.db.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {
    private final UserService userService;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    @Test
    public void shouldNotGetRecommendationsIfNoLikes() {
        Film film1 = Film.builder().name("Titanic").description("Nothing on Earth can separate them").releaseDate(LocalDate.of(1997, 11, 1)).duration(194).mpa(Rating.builder().id(3).name("PG-13").build()).build();
        Film film2 = Film.builder().name("Avatar").description("This is the new world").releaseDate(LocalDate.of(2009, 12, 17)).duration(162).mpa(Rating.builder().id(3).name("PG-13").build()).build();
        Film film3 = Film.builder().name("Fight Club").description("Intrigue. Chaos. Soap").releaseDate(LocalDate.of(1999, 9, 11)).duration(139).mpa(Rating.builder().id(4).name("R").build()).build();

        filmDbStorage.createFilm(film1);
        filmDbStorage.createFilm(film2);
        filmDbStorage.createFilm(film3);

        User user1 = User.builder().name("Mark").login("marklogin").email("mark@email.com").birthday(LocalDate.of(1992, 1, 2)).build();
        User user2 = User.builder().name("Ben").login("benlogin").email("ben@email.com").birthday(LocalDate.of(1995, 2, 4)).build();

        userDbStorage.createUser(user1);
        userDbStorage.createUser(user2);

        int userId1 = user1.getId();

        List<Film> actualRecommendations = userService.getFilmRecommendations(userId1);
        List<Film> expectedRecommendations = new ArrayList<>();
        assertEquals(expectedRecommendations, actualRecommendations);
    }

    @Test
    public void shouldGetRecommendations() {
        Film film1 = Film.builder().name("Titanic").description("Nothing on Earth can separate them").releaseDate(LocalDate.of(1997, 11, 1)).duration(194).mpa(Rating.builder().id(3).name("PG-13").build()).build();
        Film film2 = Film.builder().name("Avatar").description("This is the new world").releaseDate(LocalDate.of(2009, 12, 17)).duration(162).mpa(Rating.builder().id(3).name("PG-13").build()).build();
        Film film3 = Film.builder().name("Fight Club").description("Intrigue. Chaos. Soap").releaseDate(LocalDate.of(1999, 9, 11)).duration(139).mpa(Rating.builder().id(4).name("R").build()).build();
        Film film4 = Film.builder().name("Фильм 4").description("Nothing on Earth can separate them").releaseDate(LocalDate.of(1997, 11, 1)).duration(194).mpa(Rating.builder().id(3).name("PG-13").build()).build();
        Film film5 = Film.builder().name("Фильм 5").description("This is the new world").releaseDate(LocalDate.of(2009, 12, 17)).duration(162).mpa(Rating.builder().id(3).name("PG-13").build()).build();
        Film film6 = Film.builder().name("Фильм 6").description("Intrigue. Chaos. Soap").releaseDate(LocalDate.of(1999, 9, 11)).duration(139).mpa(Rating.builder().id(4).name("R").build()).build();

        filmDbStorage.createFilm(film1);
        filmDbStorage.createFilm(film2);
        filmDbStorage.createFilm(film3);
        filmDbStorage.createFilm(film4);
        filmDbStorage.createFilm(film5);
        filmDbStorage.createFilm(film6);

        int filmId1 = film1.getId(), filmId2 = film2.getId(), filmId3 = film3.getId(), filmId4 = film4.getId(), filmId5 = film5.getId(), filmId6 = film6.getId();

        User user1 = User.builder().name("Mark").login("marklogin").email("mark@email.com").birthday(LocalDate.of(1992, 1, 2)).build();
        User user2 = User.builder().name("Ben").login("benlogin").email("ben@email.com").birthday(LocalDate.of(1995, 2, 4)).build();

        userDbStorage.createUser(user1);
        userDbStorage.createUser(user2);

        int userId1 = user1.getId(), userId2 = user2.getId();

        filmDbStorage.addScore(filmId1, userId1, 5);
        filmDbStorage.addScore(filmId1, userId2, 6);
        filmDbStorage.addScore(filmId2, userId1, 7);
        filmDbStorage.addScore(filmId2, userId2, 8);
        filmDbStorage.addScore(filmId3, userId2, 7);
        filmDbStorage.addScore(filmId4, userId2, 2);
        filmDbStorage.addScore(filmId5, userId2, 9);
        filmDbStorage.addScore(filmId6, userId2, 6);

        List<Film> actualRecommendations = userService.getFilmRecommendations(userId1);
        List<Film> expectedRecommendations = new ArrayList<>();
        expectedRecommendations.add(film5);
        expectedRecommendations.add(film3);
        assertEquals(expectedRecommendations, actualRecommendations);
    }

    @Test
    public void shouldNotGetRecommendationsIfTheSameLikes() {
        int likeScore = 8;

        Film film1 = Film.builder().name("Titanic").description("Nothing on Earth can separate them").releaseDate(LocalDate.of(1997, 11, 1)).duration(194).mpa(Rating.builder().id(3).name("PG-13").build()).build();
        Film film2 = Film.builder().name("Avatar").description("This is the new world").releaseDate(LocalDate.of(2009, 12, 17)).duration(162).mpa(Rating.builder().id(3).name("PG-13").build()).build();
        Film film3 = Film.builder().name("Fight Club").description("Intrigue. Chaos. Soap").releaseDate(LocalDate.of(1999, 9, 11)).duration(139).mpa(Rating.builder().id(4).name("R").build()).build();

        filmDbStorage.createFilm(film1);
        filmDbStorage.createFilm(film2);
        filmDbStorage.createFilm(film3);

        int filmId1 = film1.getId(), filmId2 = film2.getId();

        User user1 = User.builder().name("Mark").login("marklogin").email("mark@email.com").birthday(LocalDate.of(1992, 1, 2)).build();
        User user2 = User.builder().name("Ben").login("benlogin").email("ben@email.com").birthday(LocalDate.of(1995, 2, 4)).build();

        userDbStorage.createUser(user1);
        userDbStorage.createUser(user2);

        int userId1 = user1.getId(), userId2 = user2.getId();

        filmDbStorage.addScore(filmId1, userId1, likeScore);
        filmDbStorage.addScore(filmId1, userId2, likeScore);
        filmDbStorage.addScore(filmId2, userId1, likeScore);
        filmDbStorage.addScore(filmId2, userId2, likeScore);

        List<Film> actualRecommendations = userService.getFilmRecommendations(userId1);
        List<Film> expectedRecommendations = new ArrayList<>();
        assertEquals(expectedRecommendations, actualRecommendations);
    }

    @AfterEach
    public void clearDb() {
        filmDbStorage.clearTableAndResetIds();
        userDbStorage.clearTablesAndResetIds();
    }
}