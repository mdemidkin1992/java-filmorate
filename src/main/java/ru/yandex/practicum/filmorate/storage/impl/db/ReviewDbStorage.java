package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.impl.db.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.utility.SqlQueries;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Component("reviewDbStorage")
@Slf4j
public class ReviewDbStorage extends DBStorage implements ReviewStorage {

    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public ReviewDbStorage(
            JdbcTemplate jdbcTemplate,
            @Qualifier("userDbStorage") UserStorage userStorage,
            @Qualifier("filmDbStorage") FilmStorage filmStorage
    ) {
        super(jdbcTemplate);
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    @Override
    public Review createReview(Review review) {
        filmStorage.getFilmById(review.getFilmId());
        userStorage.getUserById(review.getUserId());
        addReviewToDb(review);
        return review;
    }

    @Override
    public Review getReviewById(int reviewId) {
        Review review = jdbcTemplate.query(SqlQueries.GET_REVIEW, new ReviewMapper(), reviewId).stream().findAny().orElse(null);
        if (review == null) {
            log.error("Review with id {} doesn't exist", reviewId);
            throw new FilmNotFoundException("Review with id " + reviewId + " doesn't exist");
        }
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        filmStorage.getFilmById(review.getFilmId());
        userStorage.getUserById(review.getUserId());
        jdbcTemplate.update(SqlQueries.UPDATE_REVIEW,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId()
        );
        return getReviewById(review.getReviewId());
    }

    @Override
    public void deleteReviewById(int reviewId) {
        int queryResult = jdbcTemplate.update(SqlQueries.DELETE_REVIEW, reviewId);
        if (queryResult != 1) {
            throw new ReviewNotFoundException(String.format("Review id \"%d\" not found", reviewId));
        }
    }

    @Override
    public List<Review> getReviews(Integer filmId, int count) {
        if (Objects.isNull(filmId)) {
            return jdbcTemplate.query(SqlQueries.GET_REVIEWS, new ReviewMapper(), count);
        }
        return jdbcTemplate.query(SqlQueries.GET_REVIEWS_FOR_FILM, new ReviewMapper(), filmId, count);
    }

    @Override
    public void addLike(int reviewId, int userId) {
        userStorage.getUserById(userId);
        getReviewById(reviewId);
        jdbcTemplate.update(SqlQueries.ADD_REVIEW_LIKE, reviewId, userId);
    }

    @Override
    public void deleteLike(int reviewId, int userId) {
        userStorage.getUserById(userId);
        getReviewById(reviewId);
        jdbcTemplate.update(SqlQueries.DELETE_REVIEW_LIKE, reviewId, userId);
    }

    @Override
    public void addDislike(int reviewId, int userId) {
        userStorage.getUserById(userId);
        getReviewById(reviewId);
        jdbcTemplate.update(SqlQueries.ADD_REVIEW_DISLIKE, reviewId, userId);
    }

    @Override
    public void deleteDislike(int reviewId, int userId) {
        userStorage.getUserById(userId);
        getReviewById(reviewId);
        jdbcTemplate.update(SqlQueries.DELETE_REVIEW_DISLIKE, reviewId, userId);
    }

    private void addReviewToDb(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SqlQueries.ADD_REVIEW, new String[]{"REVIEW_ID"});
            stmt.setInt(1, review.getFilmId());
            stmt.setInt(2, review.getUserId());
            stmt.setString(3, review.getContent());
            stmt.setBoolean(4, review.getIsPositive());
            return stmt;
        }, keyHolder);
        review.setReviewId(keyHolder.getKey().intValue());
    }

}
