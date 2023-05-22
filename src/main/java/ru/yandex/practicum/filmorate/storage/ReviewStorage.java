package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review createReview(Review review);

    Review updateReview(Review review);

    Review getReviewById(int reviewId);

    void deleteReviewById(int reviewId);

    List<Review> getReviews(Integer filmId, int count);

    void addLike(int reviewId, int userId);

    void deleteLike(int reviewId, int userId);

    void addDislike(int reviewId, int userId);

    void deleteDislike(int reviewId, int userId);

}
