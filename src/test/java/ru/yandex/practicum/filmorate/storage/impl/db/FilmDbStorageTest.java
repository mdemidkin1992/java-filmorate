package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;

    private final DirectorDbStorage directorDbStorage;

    private final UserDbStorage userDbStorage;

    private final List<Rating> ratings = List.of(
            Rating.builder().id(1).name("G").build(),
            Rating.builder().id(2).name("PG").build(),
            Rating.builder().id(3).name("PG-13").build(),
            Rating.builder().id(4).name("R").build(),
            Rating.builder().id(5).name("NC-17").build()
    );

    @Test
    void shouldBeSize0CorrectDeleteFilm() {
        Film film = createFilm();
        filmDbStorage.createFilm(film);
        System.out.println("DEBUG: " + filmDbStorage.getFilms());
        filmDbStorage.deleteFilmById(filmDbStorage.getFilms().stream().findFirst().get().getId());
        assertEquals(filmDbStorage.getFilms().size(), 0);
    }

    @Test
    void shouldThrowExceptionIncorrectDeleteFilm() {
        assertThrows(FilmNotFoundException.class,
                () -> filmDbStorage.deleteFilmById(1));
    }

    private Film createFilm() {
        Rating rating = Rating.builder().id(1).name("G").build();
        List<Genre> genres = new ArrayList<>();
        genres.add(Genre.builder().id(1).name("Комедия").build());
        return Film.builder().name("Avatar").description("Awesome film")
                .releaseDate(LocalDate.of(2010, 10, 10)).duration(180).mpa(rating)
                .genres(genres).build();
    }

    @Test
    void shouldCreateFilm() {
        Film film = Film.builder()
                .name("Constantine")
                .description("Supernatural exorcist and demonologist John Constantine...")
                .releaseDate(LocalDate.of(2005, 8, 8))
                .duration(121)
                .mpa(ratings.get(3))
                .build();
        Film createdFilm = filmDbStorage.createFilm(film);
        assertNotNull(createdFilm);
        film.setId(createdFilm.getId());
        assertEquals(film, createdFilm);
    }

    @Test
    void shouldReturnEmptyCollectionOfAllFilms() {
        assertEquals(0, filmDbStorage.getFilms().size());
    }

    @Test
    void shouldUpdateFilm() {
        Film film = Film.builder()
                .name("Constantine")
                .description("Supernatural exorcist and demonologist John Constantine...")
                .releaseDate(LocalDate.of(2005, 8, 8))
                .duration(121)
                .mpa(ratings.get(3))
                .build();
        int id = filmDbStorage.createFilm(film).getId();
        Film updatedFilm = Film.builder()
                .id(id)
                .name("John Constantine")
                .description("Supernatural")
                .releaseDate(LocalDate.of(2008, 8, 8))
                .duration(166)
                .mpa(ratings.get(4))
                .build();
        filmDbStorage.updateFilm(updatedFilm);
        assertEquals(updatedFilm, filmDbStorage.getFilmById(id));
    }

    @Test
    void shouldReturnCollectionOfFilmsWhenTitleMatch() {
        Film filmOne = Film.builder()
                .name("Constantine")
                .description("Supernatural exorcist and demonologist John Constantine...")
                .releaseDate(LocalDate.of(2005, 8, 8))
                .duration(121)
                .mpa(ratings.get(3))
                .build();
        Film filmTwo = Film.builder()
                .name("Constantine 2")
                .description("Supernatural exorcist...")
                .releaseDate(LocalDate.of(2006, 1, 9))
                .duration(123)
                .mpa(ratings.get(4))
                .build();
        Film filmThree = Film.builder()
                .name("Constantine 3")
                .description("Supernatural...")
                .releaseDate(LocalDate.of(2007, 12, 13))
                .duration(128)
                .mpa(ratings.get(2))
                .build();
        filmOne.setId(filmDbStorage.createFilm(filmOne).getId());
        filmTwo.setId(filmDbStorage.createFilm(filmTwo).getId());
        filmThree.setId(filmDbStorage.createFilm(filmThree).getId());
        Collection<Film> expected = List.of(filmOne, filmTwo, filmThree);
        Collection<Film> titleMatch = filmDbStorage.findFilmsByTitle("Constantine".toLowerCase());
        assertEquals(3, titleMatch.size());
        assertEquals(expected, titleMatch);
    }

    @Test
    void shouldReturnCollectionOfFilmsWhenDirectorMatch() {
        Film filmOne = Film.builder()
                .name("Constantine")
                .description("Supernatural exorcist and demonologist John Constantine...")
                .releaseDate(LocalDate.of(2005, 8, 8))
                .duration(121)
                .mpa(ratings.get(3))
                .build();
        Film filmTwo = Film.builder()
                .name("Constantine 2")
                .description("Supernatural exorcist...")
                .releaseDate(LocalDate.of(2006, 1, 9))
                .duration(123)
                .mpa(ratings.get(4))
                .build();
        Film filmThree = Film.builder()
                .name("Constantine 3")
                .description("Supernatural...")
                .releaseDate(LocalDate.of(2007, 12, 13))
                .duration(128)
                .mpa(ratings.get(2))
                .build();
        filmOne.getDirectors().add(directorDbStorage.createDirector(Director.builder().name("Francis Lawrence").build()));
        filmTwo.getDirectors().add(directorDbStorage.createDirector(Director.builder().name("Francis").build()));
        filmThree.getDirectors().add(directorDbStorage.createDirector(Director.builder().name("Lawrence").build()));
        filmOne.setId(filmDbStorage.createFilm(filmOne).getId());
        filmTwo.setId(filmDbStorage.createFilm(filmTwo).getId());
        filmThree.setId(filmDbStorage.createFilm(filmThree).getId());
        Collection<Film> expected = List.of(filmOne, filmThree);
        Collection<Film> directorMatch = filmDbStorage.findFilmsByDirector("LAW".toLowerCase());
        assertEquals(2, directorMatch.size());
        assertEquals(expected, directorMatch);
    }

    @Test
    void shouldReturnCollectionOfFilmsWhenTitleOrDirectorMatch() {
        Film filmOne = Film.builder()
                .name("Constantine")
                .description("Supernatural exorcist and demonologist John Constantine...")
                .releaseDate(LocalDate.of(2005, 8, 8))
                .duration(121)
                .mpa(ratings.get(3))
                .build();
        Film filmTwo = Film.builder()
                .name("John C. 2")
                .description("Supernatural exorcist...")
                .releaseDate(LocalDate.of(2006, 1, 9))
                .duration(123)
                .mpa(ratings.get(4))
                .build();
        Film filmThree = Film.builder()
                .name("John C. 3")
                .description("Supernatural...")
                .releaseDate(LocalDate.of(2007, 12, 13))
                .duration(128)
                .mpa(ratings.get(2))
                .build();
        filmOne.getDirectors().add(directorDbStorage.createDirector(Director.builder().name("Francis Lawrence").build()));
        filmTwo.getDirectors().add(directorDbStorage.createDirector(Director.builder().name("Constantine Francis").build()));
        filmThree.getDirectors().add(directorDbStorage.createDirector(Director.builder().name("Lawrence").build()));
        filmOne.setId(filmDbStorage.createFilm(filmOne).getId());
        filmTwo.setId(filmDbStorage.createFilm(filmTwo).getId());
        filmThree.setId(filmDbStorage.createFilm(filmThree).getId());
        Collection<Film> expected = List.of(filmOne, filmTwo);
        Collection<Film> directorOrTitleMatch = filmDbStorage.findFilmsByTitleOrDirector("Constantine".toLowerCase());
        assertEquals(2, directorOrTitleMatch.size());
        assertEquals(expected, directorOrTitleMatch);
    }

    @Test
    void shouldReturnCollectionOfFilmsSortedByReleaseYearWhenDirectorIsFound() {
        Film filmOne = Film.builder()
                .name("Constantine")
                .description("Supernatural exorcist and demonologist John Constantine...")
                .releaseDate(LocalDate.of(2010, 8, 8))
                .duration(121)
                .mpa(ratings.get(3))
                .build();
        Film filmTwo = Film.builder()
                .name("John C. 2")
                .description("Supernatural exorcist...")
                .releaseDate(LocalDate.of(2006, 1, 9))
                .duration(123)
                .mpa(ratings.get(4))
                .build();
        Film filmThree = Film.builder()
                .name("John C. 3")
                .description("Supernatural...")
                .releaseDate(LocalDate.of(2007, 12, 13))
                .duration(128)
                .mpa(ratings.get(2))
                .build();
        Director director = directorDbStorage.createDirector(Director.builder().name("Francis Lawrence").build());
        filmOne.getDirectors().add(director);
        filmTwo.getDirectors().add(director);
        filmThree.getDirectors().add(director);
        filmOne.setId(filmDbStorage.createFilm(filmOne).getId());
        filmTwo.setId(filmDbStorage.createFilm(filmTwo).getId());
        filmThree.setId(filmDbStorage.createFilm(filmThree).getId());
        Collection<Film> expected = List.of(filmOne, filmTwo, filmThree);
        Collection<Film> filmsDirectorSortedByYear =
                filmDbStorage.findAllFilmsByDirectorSortedByYearOrLikes(director.getId(), "year");
        System.out.println("DEBUG:" + expected);
        System.out.println("DEBUG:" + filmsDirectorSortedByYear);
        assertEquals(3, filmsDirectorSortedByYear.size());
        assertEquals(expected, filmsDirectorSortedByYear);
    }

    @Test
    void shouldReturnCollectionOfFilmsSortedByLikesWhenDirectorIsFound() {
        int likeScore = 8;

        User user = User.builder()
                .name("User")
                .login("super-user")
                .email("su@gmail.com")
                .birthday(LocalDate.of(1988, 9, 9))
                .build();
        Film filmOne = Film.builder()
                .name("Constantine")
                .description("Supernatural exorcist and demonologist John Constantine...")
                .releaseDate(LocalDate.of(2010, 8, 8))
                .duration(121)
                .mpa(ratings.get(3))
                .build();
        Film filmTwo = Film.builder()
                .name("John C. 2")
                .description("Supernatural exorcist...")
                .releaseDate(LocalDate.of(2006, 1, 9))
                .duration(123)
                .mpa(ratings.get(4))
                .build();
        Film filmThree = Film.builder()
                .name("John C. 3")
                .description("Supernatural...")
                .releaseDate(LocalDate.of(2007, 12, 13))
                .duration(128)
                .mpa(ratings.get(2))
                .build();
        int userId = userDbStorage.createUser(user).getId();
        Director director = directorDbStorage.createDirector(Director.builder().name("Francis Lawrence").build());
        filmOne.getDirectors().add(director);
        filmTwo.getDirectors().add(director);
        filmThree.getDirectors().add(director);
        filmOne.setId(filmDbStorage.createFilm(filmOne).getId());
        filmTwo.setId(filmDbStorage.createFilm(filmTwo).getId());
        filmThree.setId(filmDbStorage.createFilm(filmThree).getId());
        filmDbStorage.addLike(filmThree.getId(), userId, likeScore);
        Collection<Film> expected = List.of(filmThree, filmOne, filmTwo);
        Collection<Film> filmsDirectorSortedByLikes =
                filmDbStorage.findAllFilmsByDirectorSortedByYearOrLikes(director.getId(), "likes");
        System.out.println("DEBUG:" + expected);
        System.out.println("DEBUG:" + filmsDirectorSortedByLikes);
        assertEquals(3, filmsDirectorSortedByLikes.size());
        assertEquals(expected, filmsDirectorSortedByLikes);
    }

    @AfterEach
    public void clearDb() {
        filmDbStorage.clearDb();
    }
}