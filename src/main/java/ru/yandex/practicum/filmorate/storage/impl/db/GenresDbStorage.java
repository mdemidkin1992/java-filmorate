package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenresStorage;
import ru.yandex.practicum.filmorate.storage.impl.db.mapper.GenreMapper;

import java.util.List;

@Component
@Slf4j
public class GenresDbStorage implements GenresStorage {

    private final JdbcTemplate jdbcTemplate;

    public GenresDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query(SqlQueries.GET_GENRES, new GenreMapper());
    }

    @Override
    public Genre getGenreById(int genreId) {
        Genre genre = jdbcTemplate.query(SqlQueries.GET_GENRE, new GenreMapper(), genreId).stream().findAny().orElse(null);
        if (genre == null) {
            log.error("Genre with id {} doesn't exist", genreId);
            throw new GenreNotFoundException("Genre with id " + genreId + " doesn't exist");
        }
        return genre;
    }
}
