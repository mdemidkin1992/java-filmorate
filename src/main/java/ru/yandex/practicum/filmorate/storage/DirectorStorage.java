package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorStorage {
    Director createDirector(Director director);

    Director updateDirector(Director director);

    Director getDirectorById(int directorId);

    List<Director> getDirectors();

    boolean deleteDirector(int directorId);
}