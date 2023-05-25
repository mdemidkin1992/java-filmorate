package ru.yandex.practicum.filmorate.storage.impl.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
@Sql({"/schema-test.sql", "/data-test.sql"})
@Import({DirectorDbStorage.class})
public class DirectorDbStorageTest {

    @Autowired
    private DirectorDbStorage directorDbStorage;

    @Test
    void shouldCreateDirector() {
        Director director = Director.builder()
                .name("Quentin Tarantino")
                .build();
        Director createdDirector = directorDbStorage.createDirector(director);
        assertNotNull(createdDirector);
        director.setId(createdDirector.getId());
        assertEquals(director, createdDirector);
    }

    @Test
    void shouldUpdateDirector() {
        Director director = Director.builder()
                .name("Quentin Tarantino")
                .build();
        int id = directorDbStorage.createDirector(director).getId();
        Director updatedDirector = Director.builder()
                .id(id)
                .name("Quentin Not Tarantino")
                .build();
        directorDbStorage.updateDirector(updatedDirector);
        assertEquals(updatedDirector, directorDbStorage.getDirectorById(id));
    }

    @Test
    void shouldGetDirectorById() {
        Director director = Director.builder()
                .name("Quentin Tarantino")
                .build();
        director.setId(directorDbStorage.createDirector(director).getId());
        assertEquals(director, directorDbStorage.getDirectorById(director.getId()));
    }

    @Test
    void shouldReturnCollectionOfAllDirectors() {
        Director directorOne = Director.builder()
                .name("Quentin Tarantino")
                .build();
        Director directorTwo = Director.builder()
                .name("Tarantino")
                .build();
        Director directorThree = Director.builder()
                .name("Quentin")
                .build();
        directorOne.setId(directorDbStorage.createDirector(directorOne).getId());
        directorTwo.setId(directorDbStorage.createDirector(directorTwo).getId());
        directorThree.setId(directorDbStorage.createDirector(directorThree).getId());
        Collection<Director> expected = List.of(directorOne, directorTwo, directorThree);
        Collection<Director> allDirectors = directorDbStorage.getDirectors();
        assertEquals(3, allDirectors.size());
        assertEquals(expected, allDirectors);
    }

    @Test
    void shouldReturnCollectionOfTwoDirectorsAfterDeleting() {
        Director directorOne = Director.builder()
                .name("Quentin Tarantino")
                .build();
        Director directorTwo = Director.builder()
                .name("Tarantino")
                .build();
        Director directorThree = Director.builder()
                .name("Quentin")
                .build();
        directorOne.setId(directorDbStorage.createDirector(directorOne).getId());
        directorTwo.setId(directorDbStorage.createDirector(directorTwo).getId());
        directorThree.setId(directorDbStorage.createDirector(directorThree).getId());
        Collection<Director> expected = List.of(directorOne, directorThree);
        directorDbStorage.deleteDirector(directorTwo.getId());
        Collection<Director> directorsAfterDeleting = directorDbStorage.getDirectors();
        assertEquals(2, directorsAfterDeleting.size());
        assertEquals(expected, directorsAfterDeleting);
    }
}