package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTest {

    private final FilmDbStorage filmDbStorage;

    @Test
    void shouldBeSize0CorrectDeleteFilm() {
        Film film = createFilm();
        filmDbStorage.createFilm(film);
        filmDbStorage.deleteFilmById(1);
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

    @AfterEach
    public void clearDb() {
        filmDbStorage.clearDb();
    }
}
