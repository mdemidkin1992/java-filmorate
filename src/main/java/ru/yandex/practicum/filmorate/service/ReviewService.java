package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.aspects.annotation.SaveUserEvent;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.util.List;

import static ru.yandex.practicum.filmorate.aspects.annotation.SaveUserEvent.None.NONE_PARAM;

@Service
@Slf4j
public class ReviewService {
    private final ReviewStorage reviewStorage;

    @Autowired
    public ReviewService(@Qualifier("reviewDbStorage") ReviewStorage reviewStorage) {
        this.reviewStorage = reviewStorage;
    }

    @SaveUserEvent(eventType = EventType.REVIEW, operation = OperationType.ADD, entityClass = Review.class)
    public Review createReview(Review review) {
        return reviewStorage.createReview(review);
    }

    public Review getReviewById(int reviewId) {
        return reviewStorage.getReviewById(reviewId);
    }

    public List<Review> getReviews(Integer filmId, int count) {
        return reviewStorage.getReviews(filmId, count);
    }

    @SaveUserEvent(eventType = EventType.REVIEW, operation = OperationType.UPDATE, entityClass = Review.class)
    public Review updateReview(Review review) {
        return reviewStorage.updateReview(review);
    }

    @SaveUserEvent(
            eventType = EventType.REVIEW, operation = OperationType.REMOVE,
            entityIdParamName = "reviewId", userIdParamName = NONE_PARAM
    )
    public void deleteReviewById(int reviewId) {
        reviewStorage.deleteReviewById(reviewId);
    }

    public void addLike(int reviewId, int userId) {
        reviewStorage.addLike(reviewId, userId);
    }

    public void addDislike(int reviewId, int userId) {
        reviewStorage.addDislike(reviewId, userId);
    }

    public void deleteLike(int reviewId, int userId) {
        reviewStorage.deleteLike(reviewId, userId);
    }

    public void deleteDislike(int reviewId, int userId) {
        reviewStorage.deleteDislike(reviewId, userId);
    }

}
