package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.List;

@Service
@Slf4j
public class DirectorService {

    private final DirectorStorage directorStorage;

    public DirectorService(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public Director createDirector(Director director) {
        return directorStorage.createDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorStorage.updateDirector(director);
    }

    public Director getDirectorById(int directorId) {
        return directorStorage.getDirectorById(directorId);
    }

    public List<Director> getDirectors() {
        return directorStorage.getDirectors();
    }

    public boolean deleteDirector(int directorId) {
        return directorStorage.deleteDirector(directorId);
    }
}