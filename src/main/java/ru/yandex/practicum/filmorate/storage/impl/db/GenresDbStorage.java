package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenresStorage;
import ru.yandex.practicum.filmorate.storage.impl.db.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.utility.SqlQueries;

import java.util.List;

@Component
@Slf4j
public class GenresDbStorage extends DBStorage implements GenresStorage {

    public GenresDbStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public List<Genre> getAllGenres() {
        return jdbcTemplate.query(SqlQueries.GET_GENRES, new GenreMapper());
    }

    @Override
    public Genre getGenreById(int genreId) {
        Genre genre = jdbcTemplate.query(SqlQueries.GET_GENRE, new GenreMapper(), genreId).stream().findAny().orElse(null);
        if (genre == null) {
            throw new GenreNotFoundException("Genre with id " + genreId + " doesn't exist");
        }
        return genre;
    }
}
