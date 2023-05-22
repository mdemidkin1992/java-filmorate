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
class FilmServiceTest {
    private final FilmService filmService;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    @Test
    public void shouldGetCommonFilms() {
        Film film1 = Film.builder().name("Titanic").description("Nothing on Earth can separate them").releaseDate(LocalDate.of(1997, 11, 1)).duration(194).mpa(Rating.builder().id(3).name("PG-13").build()).build();
        Film film2 = Film.builder().name("Avatar").description("This is the new world").releaseDate(LocalDate.of(2009, 12, 17)).duration(162).mpa(Rating.builder().id(3).name("PG-13").build()).build();
        Film film3 = Film.builder().name("Fight Club").description("Intrigue. Chaos. Soap").releaseDate(LocalDate.of(1999, 9, 11)).duration(139).mpa(Rating.builder().id(4).name("R").build()).build();

        filmDbStorage.createFilm(film1);
        filmDbStorage.createFilm(film2);
        filmDbStorage.createFilm(film3);

        int filmId1 = film1.getId(), filmId2 = film2.getId(), filmId3 = film3.getId();

        User user1 = User.builder().name("Mark").login("marklogin").email("mark@email.com").birthday(LocalDate.of(1992, 1, 2)).build();
        User user2 = User.builder().name("Ben").login("benlogin").email("ben@email.com").birthday(LocalDate.of(1995, 2, 4)).build();

        userDbStorage.createUser(user1);
        userDbStorage.createUser(user2);

        int userId1 = user1.getId(), userId2 = user2.getId();

        filmDbStorage.addLike(filmId1, userId1);
        filmDbStorage.addLike(filmId1, userId2);

        List<Film> actualCommonFilms = filmService.getCommonFilms(userId1, userId2);
        List<Film> expectedCommonFilms = new ArrayList<>();
        expectedCommonFilms.add(film1);
        assertEquals(expectedCommonFilms, actualCommonFilms);
    }

    @Test
    public void shouldGetNoFilmsWhenNoCommon() {
        Film film1 = Film.builder().name("Titanic").description("Nothing on Earth can separate them").releaseDate(LocalDate.of(1997, 11, 1)).duration(194).mpa(Rating.builder().id(3).name("PG-13").build()).build();
        Film film2 = Film.builder().name("Avatar").description("This is the new world").releaseDate(LocalDate.of(2009, 12, 17)).duration(162).mpa(Rating.builder().id(3).name("PG-13").build()).build();

        filmDbStorage.createFilm(film1);
        filmDbStorage.createFilm(film2);

        int filmId1 = film1.getId(), filmId2 = film2.getId();

        User user1 = User.builder().name("Mark").login("marklogin").email("mark@email.com").birthday(LocalDate.of(1992, 1, 2)).build();
        User user2 = User.builder().name("Ben").login("benlogin").email("ben@email.com").birthday(LocalDate.of(1995, 2, 4)).build();

        userDbStorage.createUser(user1);
        userDbStorage.createUser(user2);

        int userId1 = user1.getId(), userId2 = user2.getId();

        filmDbStorage.addLike(filmId1, userId1);
        filmDbStorage.addLike(filmId2, userId2);

        List<Film> actualRecommendations = filmService.getCommonFilms(userId1, userId2);
        List<Film> expectedRecommendations = new ArrayList<>();
        assertEquals(expectedRecommendations, actualRecommendations);
    }

    @AfterEach
    public void clearDb() {
        filmDbStorage.clearDb();
        userDbStorage.clearDb();
    }
}