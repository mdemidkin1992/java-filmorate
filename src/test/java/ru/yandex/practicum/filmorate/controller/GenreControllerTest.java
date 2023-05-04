package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class GenreControllerTest {
    private final GenreController genreController;

    @Test
    public void shouldGetAllGenres() {
        List<Genre> actualGenres = genreController.getGenres();
        List<Genre> expectedGenres = new LinkedList<>();
        expectedGenres.add(Genre.builder().id(1).name("Комедия").build());
        expectedGenres.add(Genre.builder().id(2).name("Драма").build());
        expectedGenres.add(Genre.builder().id(3).name("Мультфильм").build());
        expectedGenres.add(Genre.builder().id(4).name("Триллер").build());
        expectedGenres.add(Genre.builder().id(5).name("Документальный").build());
        expectedGenres.add(Genre.builder().id(6).name("Боевик").build());

        Assertions.assertEquals(expectedGenres, actualGenres, "Списки жарнов не совпадают");
    }


    @Test
    public void shouldGetGenreById() {
        int genreId = 1;
        Genre expectedGenre = Genre.builder().id(genreId).name("Комедия").build();
        Genre actualGenre = genreController.getGenreById(genreId);

        Assertions.assertEquals(expectedGenre, actualGenre, "Жанры не сопадают");
    }

    @Test
    public void shouldNotGetGenreWhenIdIsIncorrect() {
        int genreId = 111;
        GenreNotFoundException exception = assertThrows(GenreNotFoundException.class, () -> genreController.getGenreById(genreId));
        String expectedMessage = "Genre with id " + genreId + " doesn't exist";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}