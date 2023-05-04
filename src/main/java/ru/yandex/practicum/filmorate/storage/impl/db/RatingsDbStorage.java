package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.RatingStorage;
import ru.yandex.practicum.filmorate.storage.impl.db.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.utility.SqlQueries;

import java.util.List;

@Component
@Slf4j
public class RatingsDbStorage implements RatingStorage {
    private final JdbcTemplate jdbcTemplate;

    public RatingsDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Rating> getAllRatings() {
        return jdbcTemplate.query(SqlQueries.GET_RATINGS, new RatingMapper());
    }

    @Override
    public Rating getRatingById(int ratingId) {
        Rating mpa = jdbcTemplate.query(SqlQueries.GET_RATING, new RatingMapper(), ratingId).stream().findAny().orElse(null);
        if (mpa == null) {
            log.error("Rating with id {} doesn't exist", ratingId);
            throw new RatingNotFoundException("Rating with id " + ratingId + " doesn't exist");
        }
        return mpa;
    }
}
