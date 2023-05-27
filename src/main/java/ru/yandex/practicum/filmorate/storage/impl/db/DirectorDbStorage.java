package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.impl.db.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.utility.SqlQueries;

import java.util.List;

@Component("directorDbStorage")
@Slf4j
public class DirectorDbStorage extends DBStorage implements DirectorStorage {

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public Director createDirector(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("DIRECTORS")
                .usingGeneratedKeyColumns("DIRECTOR_ID");
        director.setId(simpleJdbcInsert.executeAndReturnKey(director.toMap()).intValue());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        int queryResult = jdbcTemplate.update(SqlQueries.UPDATE_DIRECTOR,
                director.getName(), director.getId());
        if (queryResult > 0) {
            return director;
        }
        throw new DirectorNotFoundException(String.format("Director id \"%d\" not found", director.getId()));
    }

    @Override
    public Director getDirectorById(int directorId) {
        try {
            return jdbcTemplate.queryForObject(SqlQueries.GET_DIRECTOR, new DirectorMapper(), directorId);
        } catch (DataAccessException e) {
            throw new DirectorNotFoundException(String.format("Director id \"%d\" not found", directorId));
        }
    }

    @Override
    public List<Director> getDirectors() {
        return jdbcTemplate.query(SqlQueries.GET_DIRECTORS, new DirectorMapper());
    }

    @Override
    public boolean deleteDirector(int directorId) {
        int queryResult = jdbcTemplate.update(SqlQueries.DELETE_DIRECTOR, directorId);
        if (queryResult > 0) {
            return true;
        }
        throw new DirectorNotFoundException(String.format("Director id \"%d\" not found", directorId));
    }
}