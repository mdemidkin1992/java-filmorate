package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.db.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
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
        int likeScore = 8;

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

        filmDbStorage.addLike(filmId1, userId1, likeScore);
        filmDbStorage.addLike(filmId1, userId2, likeScore);

        List<Film> actualCommonFilms = filmService.getCommonFilms(userId1, userId2);
        List<Film> expectedCommonFilms = new ArrayList<>();
        expectedCommonFilms.add(film1);
        assertEquals(expectedCommonFilms, actualCommonFilms);
    }

    @Test
    public void shouldGetNoFilmsWhenNoCommon() {
        int likeScore = 8;

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

        filmDbStorage.addLike(filmId1, userId1, likeScore);
        filmDbStorage.addLike(filmId2, userId2, likeScore);

        List<Film> actualRecommendations = filmService.getCommonFilms(userId1, userId2);
        List<Film> expectedRecommendations = new ArrayList<>();
        assertEquals(expectedRecommendations, actualRecommendations);
    }

    /*@Test
    public void setWrongLikeScore() {
        int scoreFromUser1ToFilm1 = 8;

        Film film1 = Film.builder().name("Фильм 1").description("Описание 1")
                .releaseDate(LocalDate.of(1997, 01, 1))
                .duration(194).mpa(Rating.builder().id(3).name("PG-13").build()).build();
        film1.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(3).name("Мультфильм").build())));
        User user1 = User.builder().name("User1").login("User1login").email("User1@email.com")
                .birthday(LocalDate.of(1992, 1, 2)).build();

        Film filmForTest1 = filmDbStorage.createFilm(film1);

        userDbStorage.createUser(user1);
        filmDbStorage.addLike(filmForTest1.getId(), user1.getId(), scoreFromUser1ToFilm1);


    }*/

    @Test
    public void getPopularFilmsWithParametresTest() {
        Film film1 = Film.builder().name("Фильм 1").description("Описание 1")
                .releaseDate(LocalDate.of(1997, 01, 1))
                .duration(194).mpa(Rating.builder().id(3).name("PG-13").build()).build();
        Film film2 = Film.builder().name("Фильм 2").description("Описание 2")
                .releaseDate(LocalDate.of(2009, 12, 17)).duration(162)
                .mpa(Rating.builder().id(3).name("PG-13").build()).build();
        Film film3 = Film.builder().name("Фильм 3").description("Описание 3")
                .releaseDate(LocalDate.of(1999, 9, 11)).duration(139)
                .mpa(Rating.builder().id(4).name("R").build()).build();
        Film film4 = Film.builder().name("Фильм 4").description("Intrigue. Chaos. Soap")
                .releaseDate(LocalDate.of(1999, 10, 11)).duration(50)
                .mpa(Rating.builder().id(4).name("R").build()).build();
        Film film5 = Film.builder().name("Фильм 5").description("Описание 5")
                .releaseDate(LocalDate.of(1998, 9, 11)).duration(139)
                .mpa(Rating.builder().id(4).name("R").build()).build();
        Film film6 = Film.builder().name("Фильм 6").description("Описание 6")
                .releaseDate(LocalDate.of(1999, 9, 11)).duration(139)
                .mpa(Rating.builder().id(4).name("R").build()).build();

        film1.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(3).name("Мультфильм").build())));
        film2.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(4).name("Триллер").build())));
        film3.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(1).name("Комедия").build(),
                Genre.builder().id(2).name("Драма").build())));
        film4.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(3).name("Мультфильм").build(),
                Genre.builder().id(2).name("Драма").build())));
        film5.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(1).name("Комедия").build(),
                Genre.builder().id(2).name("Драма").build())));
        film6.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(1).name("Комедия").build())));


        Film filmForTest1 = filmDbStorage.createFilm(film1);
        Film filmForTest2 = filmDbStorage.createFilm(film2);
        Film filmForTest3 = filmDbStorage.createFilm(film3);
        Film filmForTest4 = filmDbStorage.createFilm(film4);
        Film filmForTest5 = filmDbStorage.createFilm(film5);
        Film filmForTest6 = filmDbStorage.createFilm(film6);

        User user1 = User.builder().name("User1").login("User1login").email("User1@email.com")
                .birthday(LocalDate.of(1992, 1, 2)).build();
        User user2 = User.builder().name("User2").login("User2login").email("User2@email.com")
                .birthday(LocalDate.of(1995, 2, 4)).build();
        User user3 = User.builder().name("User3").login("User3login").email("User3@email.com")
                .birthday(LocalDate.of(1995, 2, 4)).build();
        User user4 = User.builder().name("User4").login("User4login").email("User4@email.com")
                .birthday(LocalDate.of(1995, 2, 4)).build();
        User user5 = User.builder().name("User5").login("User5login").email("User5@email.com")
                .birthday(LocalDate.of(1995, 2, 4)).build();

        userDbStorage.createUser(user1);
        userDbStorage.createUser(user2);
        userDbStorage.createUser(user3);
        userDbStorage.createUser(user4);
        userDbStorage.createUser(user5);

        int scoreFromUser1ToFilm1 = 8;
        int scoreFromUser2ToFilm1 = 7;
        int scoreFromUser3ToFilm1 = 9;
        int scoreFromUser4ToFilm1 = 5;
        int scoreFromUser1ToFilm2 = 3;
        int scoreFromUser2ToFilm2 = 5;
        int scoreFromUser3ToFilm2 = 8;
        int scoreFromUser4ToFilm2 = 2;
        int scoreFromUser5ToFilm2 = 1;
        int scoreFromUser1ToFilm3 = 7;
        int scoreFromUser2ToFilm3 = 7;
        int scoreFromUser3ToFilm3 = 8;
        int scoreFromUser1ToFilm4 = 5;
        int scoreFromUser2ToFilm4 = 6;
        int scoreFromUser1ToFilm5 = 2;
        int scoreFromUser2ToFilm5 = 6;
        int scoreFromUser1ToFilm6 = 9;

        filmDbStorage.addLike(filmForTest1.getId(), user1.getId(), scoreFromUser1ToFilm1);
        filmDbStorage.addLike(filmForTest1.getId(), user2.getId(), scoreFromUser2ToFilm1);
        filmDbStorage.addLike(filmForTest1.getId(), user3.getId(), scoreFromUser3ToFilm1);
        filmDbStorage.addLike(filmForTest1.getId(), user4.getId(), scoreFromUser4ToFilm1);
        filmDbStorage.addLike(filmForTest2.getId(), user1.getId(), scoreFromUser1ToFilm2);
        filmDbStorage.addLike(filmForTest2.getId(), user2.getId(), scoreFromUser2ToFilm2);
        filmDbStorage.addLike(filmForTest2.getId(), user3.getId(), scoreFromUser3ToFilm2);
        filmDbStorage.addLike(filmForTest2.getId(), user4.getId(), scoreFromUser4ToFilm2);
        filmDbStorage.addLike(filmForTest2.getId(), user5.getId(), scoreFromUser5ToFilm2);
        filmDbStorage.addLike(filmForTest3.getId(), user1.getId(), scoreFromUser1ToFilm3);
        filmDbStorage.addLike(filmForTest3.getId(), user2.getId(), scoreFromUser2ToFilm3);
        filmDbStorage.addLike(filmForTest3.getId(), user3.getId(), scoreFromUser3ToFilm3);
        filmDbStorage.addLike(filmForTest4.getId(), user1.getId(), scoreFromUser1ToFilm4);
        filmDbStorage.addLike(filmForTest4.getId(), user2.getId(), scoreFromUser2ToFilm4);
        filmDbStorage.addLike(filmForTest5.getId(), user1.getId(), scoreFromUser1ToFilm5);
        filmDbStorage.addLike(filmForTest5.getId(), user2.getId(), scoreFromUser2ToFilm5);
        filmDbStorage.addLike(filmForTest6.getId(), user1.getId(), scoreFromUser1ToFilm6);

        List<Film> popularFilmsForTest = new ArrayList<>();
        popularFilmsForTest.add(filmForTest6);
        popularFilmsForTest.add(filmForTest3);
        List<Film> popularFilms = filmDbStorage.getPopularFilmsByGenreIdAndYear(3, 1, 1999);
        assertEquals(popularFilmsForTest, popularFilms);
    }

    @Test
    public void getPopularFilmsWithoutParametresTest() {
        Film film1 = Film.builder().name("Фильм 1").description("Описание 1")
                .releaseDate(LocalDate.of(1997, 01, 1))
                .duration(194).mpa(Rating.builder().id(3).name("PG-13").build()).build();
        Film film2 = Film.builder().name("Фильм 2").description("Описание 2")
                .releaseDate(LocalDate.of(2009, 12, 17)).duration(162)
                .mpa(Rating.builder().id(3).name("PG-13").build()).build();
        Film film3 = Film.builder().name("Фильм 3").description("Описание 3")
                .releaseDate(LocalDate.of(1999, 9, 11)).duration(139)
                .mpa(Rating.builder().id(4).name("R").build()).build();
        Film film4 = Film.builder().name("Фильм 4").description("Intrigue. Chaos. Soap")
                .releaseDate(LocalDate.of(1999, 10, 11)).duration(50)
                .mpa(Rating.builder().id(4).name("R").build()).build();
        Film film5 = Film.builder().name("Фильм 5").description("Описание 5")
                .releaseDate(LocalDate.of(1998, 9, 11)).duration(139)
                .mpa(Rating.builder().id(4).name("R").build()).build();
        Film film6 = Film.builder().name("Фильм 6").description("Описание 6")
                .releaseDate(LocalDate.of(1999, 9, 11)).duration(139)
                .mpa(Rating.builder().id(4).name("R").build()).build();

        film1.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(3).name("Мультфильм").build())));
        film2.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(4).name("Триллер").build())));
        film3.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(1).name("Комедия").build(),
                Genre.builder().id(2).name("Драма").build())));
        film4.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(3).name("Мультфильм").build(),
                Genre.builder().id(2).name("Драма").build())));
        film5.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(1).name("Комедия").build(),
                Genre.builder().id(2).name("Драма").build())));
        film6.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(1).name("Комедия").build())));


        Film filmForTest1 = filmDbStorage.createFilm(film1);
        Film filmForTest2 = filmDbStorage.createFilm(film2);
        Film filmForTest3 = filmDbStorage.createFilm(film3);
        Film filmForTest4 = filmDbStorage.createFilm(film4);
        Film filmForTest5 = filmDbStorage.createFilm(film5);
        Film filmForTest6 = filmDbStorage.createFilm(film6);

        User user1 = User.builder().name("User1").login("User1login").email("User1@email.com")
                .birthday(LocalDate.of(1992, 1, 2)).build();
        User user2 = User.builder().name("User2").login("User2login").email("User2@email.com")
                .birthday(LocalDate.of(1995, 2, 4)).build();
        User user3 = User.builder().name("User3").login("User3login").email("User3@email.com")
                .birthday(LocalDate.of(1995, 2, 4)).build();
        User user4 = User.builder().name("User4").login("User4login").email("User4@email.com")
                .birthday(LocalDate.of(1995, 2, 4)).build();
        User user5 = User.builder().name("User5").login("User5login").email("User5@email.com")
                .birthday(LocalDate.of(1995, 2, 4)).build();

        userDbStorage.createUser(user1);
        userDbStorage.createUser(user2);
        userDbStorage.createUser(user3);
        userDbStorage.createUser(user4);
        userDbStorage.createUser(user5);

        int scoreFromUser1ToFilm1 = 8;
        int scoreFromUser2ToFilm1 = 7;
        int scoreFromUser3ToFilm1 = 9;
        int scoreFromUser4ToFilm1 = 5;
        int scoreFromUser1ToFilm2 = 3;
        int scoreFromUser2ToFilm2 = 5;
        int scoreFromUser3ToFilm2 = 8;
        int scoreFromUser4ToFilm2 = 2;
        int scoreFromUser5ToFilm2 = 1;
        int scoreFromUser1ToFilm3 = 7;
        int scoreFromUser2ToFilm3 = 7;
        int scoreFromUser3ToFilm3 = 8;
        int scoreFromUser1ToFilm4 = 5;
        int scoreFromUser2ToFilm4 = 6;
        int scoreFromUser1ToFilm5 = 2;
        int scoreFromUser2ToFilm5 = 6;
        int scoreFromUser1ToFilm6 = 9;

        filmDbStorage.addLike(filmForTest1.getId(), user1.getId(), scoreFromUser1ToFilm1);
        filmDbStorage.addLike(filmForTest1.getId(), user2.getId(), scoreFromUser2ToFilm1);
        filmDbStorage.addLike(filmForTest1.getId(), user3.getId(), scoreFromUser3ToFilm1);
        filmDbStorage.addLike(filmForTest1.getId(), user4.getId(), scoreFromUser4ToFilm1);
        filmDbStorage.addLike(filmForTest2.getId(), user1.getId(), scoreFromUser1ToFilm2);
        filmDbStorage.addLike(filmForTest2.getId(), user2.getId(), scoreFromUser2ToFilm2);
        filmDbStorage.addLike(filmForTest2.getId(), user3.getId(), scoreFromUser3ToFilm2);
        filmDbStorage.addLike(filmForTest2.getId(), user4.getId(), scoreFromUser4ToFilm2);
        filmDbStorage.addLike(filmForTest2.getId(), user5.getId(), scoreFromUser5ToFilm2);
        filmDbStorage.addLike(filmForTest3.getId(), user1.getId(), scoreFromUser1ToFilm3);
        filmDbStorage.addLike(filmForTest3.getId(), user2.getId(), scoreFromUser2ToFilm3);
        filmDbStorage.addLike(filmForTest3.getId(), user3.getId(), scoreFromUser3ToFilm3);
        filmDbStorage.addLike(filmForTest4.getId(), user1.getId(), scoreFromUser1ToFilm4);
        filmDbStorage.addLike(filmForTest4.getId(), user2.getId(), scoreFromUser2ToFilm4);
        filmDbStorage.addLike(filmForTest5.getId(), user1.getId(), scoreFromUser1ToFilm5);
        filmDbStorage.addLike(filmForTest5.getId(), user2.getId(), scoreFromUser2ToFilm5);
        filmDbStorage.addLike(filmForTest6.getId(), user1.getId(), scoreFromUser1ToFilm6);

        List<Film> popularFilmsForTest = new ArrayList<>();
        popularFilmsForTest.add(filmForTest6);
        popularFilmsForTest.add(filmForTest1);
        popularFilmsForTest.add(filmForTest3);
        popularFilmsForTest.add(filmForTest4);
        popularFilmsForTest.add(filmForTest5);
        popularFilmsForTest.add(filmForTest2);
        List<Film> popularFilms = filmDbStorage.getPopularFilmsByGenreIdAndYear(10, null, null);
        assertEquals(popularFilmsForTest, popularFilms);
    }

    @Test
    public void getPopularFilmsWithCountAndYearParametresTest() {
        Film film1 = Film.builder().name("Фильм 1").description("Описание 1")
                .releaseDate(LocalDate.of(1997, 01, 1))
                .duration(194).mpa(Rating.builder().id(3).name("PG-13").build()).build();
        Film film2 = Film.builder().name("Фильм 2").description("Описание 2")
                .releaseDate(LocalDate.of(2009, 12, 17)).duration(162)
                .mpa(Rating.builder().id(3).name("PG-13").build()).build();
        Film film3 = Film.builder().name("Фильм 3").description("Описание 3")
                .releaseDate(LocalDate.of(1999, 9, 11)).duration(139)
                .mpa(Rating.builder().id(4).name("R").build()).build();
        Film film4 = Film.builder().name("Фильм 4").description("Intrigue. Chaos. Soap")
                .releaseDate(LocalDate.of(1999, 10, 11)).duration(50)
                .mpa(Rating.builder().id(4).name("R").build()).build();
        Film film5 = Film.builder().name("Фильм 5").description("Описание 5")
                .releaseDate(LocalDate.of(1998, 9, 11)).duration(139)
                .mpa(Rating.builder().id(4).name("R").build()).build();
        Film film6 = Film.builder().name("Фильм 6").description("Описание 6")
                .releaseDate(LocalDate.of(1999, 9, 11)).duration(139)
                .mpa(Rating.builder().id(4).name("R").build()).build();

        film1.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(3).name("Мультфильм").build())));
        film2.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(4).name("Триллер").build())));
        film3.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(1).name("Комедия").build(),
                Genre.builder().id(2).name("Драма").build())));
        film4.setGenres(new ArrayList<>(Arrays.asList(
                Genre.builder().id(2).name("Драма").build(),
                Genre.builder().id(3).name("Мультфильм").build()
        )));
        film5.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(1).name("Комедия").build(),
                Genre.builder().id(2).name("Драма").build())));
        film6.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(1).name("Комедия").build())));


        Film filmForTest1 = filmDbStorage.createFilm(film1);
        Film filmForTest2 = filmDbStorage.createFilm(film2);
        Film filmForTest3 = filmDbStorage.createFilm(film3);
        Film filmForTest4 = filmDbStorage.createFilm(film4);
        Film filmForTest5 = filmDbStorage.createFilm(film5);
        Film filmForTest6 = filmDbStorage.createFilm(film6);

        User user1 = User.builder().name("User1").login("User1login").email("User1@email.com")
                .birthday(LocalDate.of(1992, 1, 2)).build();
        User user2 = User.builder().name("User2").login("User2login").email("User2@email.com")
                .birthday(LocalDate.of(1995, 2, 4)).build();
        User user3 = User.builder().name("User3").login("User3login").email("User3@email.com")
                .birthday(LocalDate.of(1995, 2, 4)).build();
        User user4 = User.builder().name("User4").login("User4login").email("User4@email.com")
                .birthday(LocalDate.of(1995, 2, 4)).build();
        User user5 = User.builder().name("User5").login("User5login").email("User5@email.com")
                .birthday(LocalDate.of(1995, 2, 4)).build();

        userDbStorage.createUser(user1);
        userDbStorage.createUser(user2);
        userDbStorage.createUser(user3);
        userDbStorage.createUser(user4);
        userDbStorage.createUser(user5);

        int scoreFromUser1ToFilm1 = 8;
        int scoreFromUser2ToFilm1 = 7;
        int scoreFromUser3ToFilm1 = 9;
        int scoreFromUser4ToFilm1 = 5;
        int scoreFromUser1ToFilm2 = 3;
        int scoreFromUser2ToFilm2 = 5;
        int scoreFromUser3ToFilm2 = 8;
        int scoreFromUser4ToFilm2 = 2;
        int scoreFromUser5ToFilm2 = 1;
        int scoreFromUser1ToFilm3 = 7;
        int scoreFromUser2ToFilm3 = 7;
        int scoreFromUser3ToFilm3 = 8;
        int scoreFromUser1ToFilm4 = 5;
        int scoreFromUser2ToFilm4 = 6;
        int scoreFromUser1ToFilm5 = 2;
        int scoreFromUser2ToFilm5 = 6;
        int scoreFromUser1ToFilm6 = 9;

        filmDbStorage.addLike(filmForTest1.getId(), user1.getId(), scoreFromUser1ToFilm1);
        filmDbStorage.addLike(filmForTest1.getId(), user2.getId(), scoreFromUser2ToFilm1);
        filmDbStorage.addLike(filmForTest1.getId(), user3.getId(), scoreFromUser3ToFilm1);
        filmDbStorage.addLike(filmForTest1.getId(), user4.getId(), scoreFromUser4ToFilm1);
        filmDbStorage.addLike(filmForTest2.getId(), user1.getId(), scoreFromUser1ToFilm2);
        filmDbStorage.addLike(filmForTest2.getId(), user2.getId(), scoreFromUser2ToFilm2);
        filmDbStorage.addLike(filmForTest2.getId(), user3.getId(), scoreFromUser3ToFilm2);
        filmDbStorage.addLike(filmForTest2.getId(), user4.getId(), scoreFromUser4ToFilm2);
        filmDbStorage.addLike(filmForTest2.getId(), user5.getId(), scoreFromUser5ToFilm2);
        filmDbStorage.addLike(filmForTest3.getId(), user1.getId(), scoreFromUser1ToFilm3);
        filmDbStorage.addLike(filmForTest3.getId(), user2.getId(), scoreFromUser2ToFilm3);
        filmDbStorage.addLike(filmForTest3.getId(), user3.getId(), scoreFromUser3ToFilm3);
        filmDbStorage.addLike(filmForTest4.getId(), user1.getId(), scoreFromUser1ToFilm4);
        filmDbStorage.addLike(filmForTest4.getId(), user2.getId(), scoreFromUser2ToFilm4);
        filmDbStorage.addLike(filmForTest5.getId(), user1.getId(), scoreFromUser1ToFilm5);
        filmDbStorage.addLike(filmForTest5.getId(), user2.getId(), scoreFromUser2ToFilm5);
        filmDbStorage.addLike(filmForTest6.getId(), user1.getId(), scoreFromUser1ToFilm6);

        List<Film> popularFilmsForTest = new ArrayList<>();
        popularFilmsForTest.add(filmForTest6);
        popularFilmsForTest.add(filmForTest3);
        popularFilmsForTest.add(filmForTest4);
        List<Film> popularFilms = filmDbStorage.getPopularFilmsByGenreIdAndYear(3, null, 1999);
        assertEquals(popularFilmsForTest, popularFilms);
    }

    @Test
    public void getPopularFilmsWithCountAndGenreIdTest() {
        Film film1 = Film.builder().name("Фильм 1").description("Описание 1")
                .releaseDate(LocalDate.of(1997, 01, 1))
                .duration(194).mpa(Rating.builder().id(3).name("PG-13").build()).build();
        Film film2 = Film.builder().name("Фильм 2").description("Описание 2")
                .releaseDate(LocalDate.of(2009, 12, 17)).duration(162)
                .mpa(Rating.builder().id(3).name("PG-13").build()).build();
        Film film3 = Film.builder().name("Фильм 3").description("Описание 3")
                .releaseDate(LocalDate.of(1999, 9, 11)).duration(139)
                .mpa(Rating.builder().id(4).name("R").build()).build();
        Film film4 = Film.builder().name("Фильм 4").description("Intrigue. Chaos. Soap")
                .releaseDate(LocalDate.of(1999, 10, 11)).duration(50)
                .mpa(Rating.builder().id(4).name("R").build()).build();
        Film film5 = Film.builder().name("Фильм 5").description("Описание 5")
                .releaseDate(LocalDate.of(1998, 9, 11)).duration(139)
                .mpa(Rating.builder().id(4).name("R").build()).build();
        Film film6 = Film.builder().name("Фильм 6").description("Описание 6")
                .releaseDate(LocalDate.of(1999, 9, 11)).duration(139)
                .mpa(Rating.builder().id(4).name("R").build()).build();

        film1.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(3).name("Мультфильм").build())));
        film2.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(4).name("Триллер").build())));
        film3.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(1).name("Комедия").build(),
                Genre.builder().id(2).name("Драма").build())));
        film4.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(3).name("Мультфильм").build(),
                Genre.builder().id(2).name("Драма").build())));
        film5.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(1).name("Комедия").build(),
                Genre.builder().id(2).name("Драма").build())));
        film6.setGenres(new ArrayList<>(Arrays.asList(Genre.builder().id(1).name("Комедия").build())));


        Film filmForTest1 = filmDbStorage.createFilm(film1);
        Film filmForTest2 = filmDbStorage.createFilm(film2);
        Film filmForTest3 = filmDbStorage.createFilm(film3);
        Film filmForTest4 = filmDbStorage.createFilm(film4);
        Film filmForTest5 = filmDbStorage.createFilm(film5);
        Film filmForTest6 = filmDbStorage.createFilm(film6);

        User user1 = User.builder().name("User1").login("User1login").email("User1@email.com")
                .birthday(LocalDate.of(1992, 1, 2)).build();
        User user2 = User.builder().name("User2").login("User2login").email("User2@email.com")
                .birthday(LocalDate.of(1995, 2, 4)).build();
        User user3 = User.builder().name("User3").login("User3login").email("User3@email.com")
                .birthday(LocalDate.of(1995, 2, 4)).build();
        User user4 = User.builder().name("User4").login("User4login").email("User4@email.com")
                .birthday(LocalDate.of(1995, 2, 4)).build();
        User user5 = User.builder().name("User5").login("User5login").email("User5@email.com")
                .birthday(LocalDate.of(1995, 2, 4)).build();

        userDbStorage.createUser(user1);
        userDbStorage.createUser(user2);
        userDbStorage.createUser(user3);
        userDbStorage.createUser(user4);
        userDbStorage.createUser(user5);

        int scoreFromUser1ToFilm1 = 8;
        int scoreFromUser2ToFilm1 = 7;
        int scoreFromUser3ToFilm1 = 9;
        int scoreFromUser4ToFilm1 = 5;
        int scoreFromUser1ToFilm2 = 3;
        int scoreFromUser2ToFilm2 = 5;
        int scoreFromUser3ToFilm2 = 8;
        int scoreFromUser4ToFilm2 = 2;
        int scoreFromUser5ToFilm2 = 1;
        int scoreFromUser1ToFilm3 = 7;
        int scoreFromUser2ToFilm3 = 7;
        int scoreFromUser3ToFilm3 = 8;
        int scoreFromUser1ToFilm4 = 5;
        int scoreFromUser2ToFilm4 = 6;
        int scoreFromUser1ToFilm5 = 2;
        int scoreFromUser2ToFilm5 = 6;
        int scoreFromUser1ToFilm6 = 9;

        filmDbStorage.addLike(filmForTest1.getId(), user1.getId(), scoreFromUser1ToFilm1);
        filmDbStorage.addLike(filmForTest1.getId(), user2.getId(), scoreFromUser2ToFilm1);
        filmDbStorage.addLike(filmForTest1.getId(), user3.getId(), scoreFromUser3ToFilm1);
        filmDbStorage.addLike(filmForTest1.getId(), user4.getId(), scoreFromUser4ToFilm1);
        filmDbStorage.addLike(filmForTest2.getId(), user1.getId(), scoreFromUser1ToFilm2);
        filmDbStorage.addLike(filmForTest2.getId(), user2.getId(), scoreFromUser2ToFilm2);
        filmDbStorage.addLike(filmForTest2.getId(), user3.getId(), scoreFromUser3ToFilm2);
        filmDbStorage.addLike(filmForTest2.getId(), user4.getId(), scoreFromUser4ToFilm2);
        filmDbStorage.addLike(filmForTest2.getId(), user5.getId(), scoreFromUser5ToFilm2);
        filmDbStorage.addLike(filmForTest3.getId(), user1.getId(), scoreFromUser1ToFilm3);
        filmDbStorage.addLike(filmForTest3.getId(), user2.getId(), scoreFromUser2ToFilm3);
        filmDbStorage.addLike(filmForTest3.getId(), user3.getId(), scoreFromUser3ToFilm3);
        filmDbStorage.addLike(filmForTest4.getId(), user1.getId(), scoreFromUser1ToFilm4);
        filmDbStorage.addLike(filmForTest4.getId(), user2.getId(), scoreFromUser2ToFilm4);
        filmDbStorage.addLike(filmForTest5.getId(), user1.getId(), scoreFromUser1ToFilm5);
        filmDbStorage.addLike(filmForTest5.getId(), user2.getId(), scoreFromUser2ToFilm5);
        filmDbStorage.addLike(filmForTest6.getId(), user1.getId(), scoreFromUser1ToFilm6);

        List<Film> popularFilmsForTest = new ArrayList<>();
        popularFilmsForTest.add(filmForTest6);
        popularFilmsForTest.add(filmForTest3);
        popularFilmsForTest.add(filmForTest5);
        List<Film> popularFilms = filmDbStorage.getPopularFilmsByGenreIdAndYear(10, 1, null);
        assertEquals(popularFilmsForTest, popularFilms);
    }

    @AfterEach
    public void clearDb() {
        filmDbStorage.clearDb();
        userDbStorage.clearDb();
    }
}