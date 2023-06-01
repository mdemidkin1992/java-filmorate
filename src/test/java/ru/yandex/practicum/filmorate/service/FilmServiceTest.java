package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.db.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.db.UserDbStorage;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static util.CustomEasyRandom.*;

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

        Film film1 = nextFilm();
        Film film2 = nextFilm();
        Film film3 = nextFilm();
        filmDbStorage.createFilm(film1);
        filmDbStorage.createFilm(film2);
        filmDbStorage.createFilm(film3);

        int filmId1 = film1.getId();

        User user1 = nextUser();
        User user2 = nextUser();

        userDbStorage.createUser(user1);
        userDbStorage.createUser(user2);

        int userId1 = user1.getId();
        int userId2 = user2.getId();

        filmDbStorage.addScore(filmId1, userId1, likeScore);
        filmDbStorage.addScore(filmId1, userId2, likeScore);

        List<Film> actualCommonFilms = filmService.getCommonFilms(userId1, userId2);
        List<Film> expectedCommonFilms = new ArrayList<>();
        expectedCommonFilms.add(film1);
        assertEquals(expectedCommonFilms, actualCommonFilms);
    }

    @Test
    public void shouldGetNoFilmsWhenNoCommon() {
        int likeScore = 8;

        Film film1 = nextFilm();
        Film film2 = nextFilm();

        filmDbStorage.createFilm(film1);
        filmDbStorage.createFilm(film2);

        int filmId1 = film1.getId(), filmId2 = film2.getId();

        User user1 = nextUser();
        User user2 = nextUser();

        userDbStorage.createUser(user1);
        userDbStorage.createUser(user2);

        int userId1 = user1.getId(), userId2 = user2.getId();

        filmDbStorage.addScore(filmId1, userId1, likeScore);
        filmDbStorage.addScore(filmId2, userId2, likeScore);

        List<Film> actualRecommendations = filmService.getCommonFilms(userId1, userId2);
        List<Film> expectedRecommendations = new ArrayList<>();
        assertEquals(expectedRecommendations, actualRecommendations);
    }

    @Test
    public void getPopularFilmsWithParametresTest() {
        Film film1 = nextFilm(1997);
        Film film2 = nextFilm(2009);
        Film film3 = nextFilm(1999);
        Film film4 = nextFilm(1999);
        Film film5 = nextFilm(1998);
        Film film6 = nextFilm(1999);

        film1.setGenres(List.of(getGenre(3)));
        film2.setGenres(List.of(getGenre(4)));
        film3.setGenres(List.of(getGenre(1), getGenre(2)));
        film4.setGenres(List.of(getGenre(3), getGenre(2)));
        film5.setGenres(List.of(getGenre(1), getGenre(2)));
        film6.setGenres(List.of(getGenre(1)));

        Film filmForTest1 = filmDbStorage.createFilm(film1);
        Film filmForTest2 = filmDbStorage.createFilm(film2);
        Film filmForTest3 = filmDbStorage.createFilm(film3);
        Film filmForTest4 = filmDbStorage.createFilm(film4);
        Film filmForTest5 = filmDbStorage.createFilm(film5);
        Film filmForTest6 = filmDbStorage.createFilm(film6);

        User user1 = nextUser();
        User user2 = nextUser();
        User user3 = nextUser();
        User user4 = nextUser();
        User user5 = nextUser();

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

        filmDbStorage.addScore(filmForTest1.getId(), user1.getId(), scoreFromUser1ToFilm1);
        filmDbStorage.addScore(filmForTest1.getId(), user2.getId(), scoreFromUser2ToFilm1);
        filmDbStorage.addScore(filmForTest1.getId(), user3.getId(), scoreFromUser3ToFilm1);
        filmDbStorage.addScore(filmForTest1.getId(), user4.getId(), scoreFromUser4ToFilm1);
        filmDbStorage.addScore(filmForTest2.getId(), user1.getId(), scoreFromUser1ToFilm2);
        filmDbStorage.addScore(filmForTest2.getId(), user2.getId(), scoreFromUser2ToFilm2);
        filmDbStorage.addScore(filmForTest2.getId(), user3.getId(), scoreFromUser3ToFilm2);
        filmDbStorage.addScore(filmForTest2.getId(), user4.getId(), scoreFromUser4ToFilm2);
        filmDbStorage.addScore(filmForTest2.getId(), user5.getId(), scoreFromUser5ToFilm2);
        filmDbStorage.addScore(filmForTest3.getId(), user1.getId(), scoreFromUser1ToFilm3);
        filmDbStorage.addScore(filmForTest3.getId(), user2.getId(), scoreFromUser2ToFilm3);
        filmDbStorage.addScore(filmForTest3.getId(), user3.getId(), scoreFromUser3ToFilm3);
        filmDbStorage.addScore(filmForTest4.getId(), user1.getId(), scoreFromUser1ToFilm4);
        filmDbStorage.addScore(filmForTest4.getId(), user2.getId(), scoreFromUser2ToFilm4);
        filmDbStorage.addScore(filmForTest5.getId(), user1.getId(), scoreFromUser1ToFilm5);
        filmDbStorage.addScore(filmForTest5.getId(), user2.getId(), scoreFromUser2ToFilm5);
        filmDbStorage.addScore(filmForTest6.getId(), user1.getId(), scoreFromUser1ToFilm6);

        List<Film> popularFilmsForTest = new ArrayList<>();
        popularFilmsForTest.add(filmForTest6);
        popularFilmsForTest.add(filmForTest3);
        List<Film> popularFilms = filmDbStorage.getPopularFilmsByGenreIdAndYear(3, 1, 1999);
        assertEquals(popularFilmsForTest, popularFilms);
    }

    @Test
    public void getPopularFilmsWithoutParametresTest() {
        Film film1 = nextFilm(1997);
        Film film2 = nextFilm(2009);
        Film film3 = nextFilm(1999);
        Film film4 = nextFilm(1999);
        Film film5 = nextFilm(1998);
        Film film6 = nextFilm(1999);

        film1.setGenres(List.of(getGenre(3)));
        film2.setGenres(List.of(getGenre(4)));
        film3.setGenres(List.of(getGenre(1), getGenre(2)));

        film4.setGenres(List.of(getGenre(3), getGenre(2)));
        film5.setGenres(List.of(getGenre(1), getGenre(2)));
        film6.setGenres(List.of(getGenre(1)));

        Film filmForTest1 = filmDbStorage.createFilm(film1);
        Film filmForTest2 = filmDbStorage.createFilm(film2);
        Film filmForTest3 = filmDbStorage.createFilm(film3);
        Film filmForTest4 = filmDbStorage.createFilm(film4);
        Film filmForTest5 = filmDbStorage.createFilm(film5);
        Film filmForTest6 = filmDbStorage.createFilm(film6);

        User user1 = nextUser();
        User user2 = nextUser();
        User user3 = nextUser();
        User user4 = nextUser();
        User user5 = nextUser();

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

        filmDbStorage.addScore(filmForTest1.getId(), user1.getId(), scoreFromUser1ToFilm1);
        filmDbStorage.addScore(filmForTest1.getId(), user2.getId(), scoreFromUser2ToFilm1);
        filmDbStorage.addScore(filmForTest1.getId(), user3.getId(), scoreFromUser3ToFilm1);
        filmDbStorage.addScore(filmForTest1.getId(), user4.getId(), scoreFromUser4ToFilm1);
        filmDbStorage.addScore(filmForTest2.getId(), user1.getId(), scoreFromUser1ToFilm2);
        filmDbStorage.addScore(filmForTest2.getId(), user2.getId(), scoreFromUser2ToFilm2);
        filmDbStorage.addScore(filmForTest2.getId(), user3.getId(), scoreFromUser3ToFilm2);
        filmDbStorage.addScore(filmForTest2.getId(), user4.getId(), scoreFromUser4ToFilm2);
        filmDbStorage.addScore(filmForTest2.getId(), user5.getId(), scoreFromUser5ToFilm2);
        filmDbStorage.addScore(filmForTest3.getId(), user1.getId(), scoreFromUser1ToFilm3);
        filmDbStorage.addScore(filmForTest3.getId(), user2.getId(), scoreFromUser2ToFilm3);
        filmDbStorage.addScore(filmForTest3.getId(), user3.getId(), scoreFromUser3ToFilm3);
        filmDbStorage.addScore(filmForTest4.getId(), user1.getId(), scoreFromUser1ToFilm4);
        filmDbStorage.addScore(filmForTest4.getId(), user2.getId(), scoreFromUser2ToFilm4);
        filmDbStorage.addScore(filmForTest5.getId(), user1.getId(), scoreFromUser1ToFilm5);
        filmDbStorage.addScore(filmForTest5.getId(), user2.getId(), scoreFromUser2ToFilm5);
        filmDbStorage.addScore(filmForTest6.getId(), user1.getId(), scoreFromUser1ToFilm6);

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
        Film film1 = nextFilm(1997);
        Film film2 = nextFilm(2009);
        Film film3 = nextFilm(1999);
        Film film4 = nextFilm(1999);
        Film film5 = nextFilm(1998);
        Film film6 = nextFilm(1999);

        film3.setGenres(List.of(
                getGenre(1),
                getGenre(2)
        ));
        film4.setGenres(List.of(
                getGenre(2),
                getGenre(3)
        ));
        film6.setGenres(List.of(getGenre(2)));

        Film filmForTest1 = filmDbStorage.createFilm(film1);
        Film filmForTest2 = filmDbStorage.createFilm(film2);
        Film filmForTest3 = filmDbStorage.createFilm(film3);
        Film filmForTest4 = filmDbStorage.createFilm(film4);
        Film filmForTest5 = filmDbStorage.createFilm(film5);
        Film filmForTest6 = filmDbStorage.createFilm(film6);

        User user1 = nextUser();
        User user2 = nextUser();
        User user3 = nextUser();
        User user4 = nextUser();
        User user5 = nextUser();

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

        filmDbStorage.addScore(filmForTest1.getId(), user1.getId(), scoreFromUser1ToFilm1);
        filmDbStorage.addScore(filmForTest1.getId(), user2.getId(), scoreFromUser2ToFilm1);
        filmDbStorage.addScore(filmForTest1.getId(), user3.getId(), scoreFromUser3ToFilm1);
        filmDbStorage.addScore(filmForTest1.getId(), user4.getId(), scoreFromUser4ToFilm1);
        filmDbStorage.addScore(filmForTest2.getId(), user1.getId(), scoreFromUser1ToFilm2);
        filmDbStorage.addScore(filmForTest2.getId(), user2.getId(), scoreFromUser2ToFilm2);
        filmDbStorage.addScore(filmForTest2.getId(), user3.getId(), scoreFromUser3ToFilm2);
        filmDbStorage.addScore(filmForTest2.getId(), user4.getId(), scoreFromUser4ToFilm2);
        filmDbStorage.addScore(filmForTest2.getId(), user5.getId(), scoreFromUser5ToFilm2);
        filmDbStorage.addScore(filmForTest3.getId(), user1.getId(), scoreFromUser1ToFilm3);
        filmDbStorage.addScore(filmForTest3.getId(), user2.getId(), scoreFromUser2ToFilm3);
        filmDbStorage.addScore(filmForTest3.getId(), user3.getId(), scoreFromUser3ToFilm3);
        filmDbStorage.addScore(filmForTest4.getId(), user1.getId(), scoreFromUser1ToFilm4);
        filmDbStorage.addScore(filmForTest4.getId(), user2.getId(), scoreFromUser2ToFilm4);
        filmDbStorage.addScore(filmForTest5.getId(), user1.getId(), scoreFromUser1ToFilm5);
        filmDbStorage.addScore(filmForTest5.getId(), user2.getId(), scoreFromUser2ToFilm5);
        filmDbStorage.addScore(filmForTest6.getId(), user1.getId(), scoreFromUser1ToFilm6);

        List<Film> popularFilmsForTest = new ArrayList<>();
        popularFilmsForTest.add(filmForTest6);
        popularFilmsForTest.add(filmForTest3);
        popularFilmsForTest.add(filmForTest4);
        List<Film> popularFilms = filmDbStorage.getPopularFilmsByGenreIdAndYear(3, null, 1999);
        assertEquals(popularFilmsForTest, popularFilms);
    }

    @Test
    public void getPopularFilmsWithCountAndGenreIdTest() {
        Film film1 = nextFilm();
        Film film2 = nextFilm();
        Film film3 = nextFilm();
        Film film4 = nextFilm();
        Film film5 = nextFilm();
        Film film6 = nextFilm();

        film1.setGenres(List.of(getGenre(3)));
        film2.setGenres(List.of(getGenre(4)));
        film3.setGenres(List.of(getGenre(1), getGenre(2)));
        film4.setGenres(List.of(getGenre(3), getGenre(2)));
        film5.setGenres(List.of(getGenre(1), getGenre(2)));
        film6.setGenres(List.of(getGenre(1)));

        Film filmForTest1 = filmDbStorage.createFilm(film1);
        Film filmForTest2 = filmDbStorage.createFilm(film2);
        Film filmForTest3 = filmDbStorage.createFilm(film3);
        Film filmForTest4 = filmDbStorage.createFilm(film4);
        Film filmForTest5 = filmDbStorage.createFilm(film5);
        Film filmForTest6 = filmDbStorage.createFilm(film6);

        User user1 = nextUser();
        User user2 = nextUser();
        User user3 = nextUser();
        User user4 = nextUser();
        User user5 = nextUser();

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

        filmDbStorage.addScore(filmForTest1.getId(), user1.getId(), scoreFromUser1ToFilm1);
        filmDbStorage.addScore(filmForTest1.getId(), user2.getId(), scoreFromUser2ToFilm1);
        filmDbStorage.addScore(filmForTest1.getId(), user3.getId(), scoreFromUser3ToFilm1);
        filmDbStorage.addScore(filmForTest1.getId(), user4.getId(), scoreFromUser4ToFilm1);
        filmDbStorage.addScore(filmForTest2.getId(), user1.getId(), scoreFromUser1ToFilm2);
        filmDbStorage.addScore(filmForTest2.getId(), user2.getId(), scoreFromUser2ToFilm2);
        filmDbStorage.addScore(filmForTest2.getId(), user3.getId(), scoreFromUser3ToFilm2);
        filmDbStorage.addScore(filmForTest2.getId(), user4.getId(), scoreFromUser4ToFilm2);
        filmDbStorage.addScore(filmForTest2.getId(), user5.getId(), scoreFromUser5ToFilm2);
        filmDbStorage.addScore(filmForTest3.getId(), user1.getId(), scoreFromUser1ToFilm3);
        filmDbStorage.addScore(filmForTest3.getId(), user2.getId(), scoreFromUser2ToFilm3);
        filmDbStorage.addScore(filmForTest3.getId(), user3.getId(), scoreFromUser3ToFilm3);
        filmDbStorage.addScore(filmForTest4.getId(), user1.getId(), scoreFromUser1ToFilm4);
        filmDbStorage.addScore(filmForTest4.getId(), user2.getId(), scoreFromUser2ToFilm4);
        filmDbStorage.addScore(filmForTest5.getId(), user1.getId(), scoreFromUser1ToFilm5);
        filmDbStorage.addScore(filmForTest5.getId(), user2.getId(), scoreFromUser2ToFilm5);
        filmDbStorage.addScore(filmForTest6.getId(), user1.getId(), scoreFromUser1ToFilm6);

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