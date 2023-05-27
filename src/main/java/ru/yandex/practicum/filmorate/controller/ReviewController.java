package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/reviews")
@Validated
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public List<Review> getReviews(
            @RequestParam(required = false) Integer filmId,
            @RequestParam(defaultValue = "10", required = false) int count
    ) {
        return reviewService.getReviews(filmId, count);
    }

    @PostMapping
    public Review createReview(@NotNull @Valid @RequestBody Review review) {
        return reviewService.createReview(review);
    }

    @DeleteMapping("{id}")
    public void deleteReview(@PathVariable("id") int reviewId) {
        reviewService.deleteReviewById(reviewId);
    }

    @PutMapping
    public Review updateReview(@NotNull @Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @GetMapping("{id}")
    public Review getReviewById(@PathVariable("id") int reviewId) {
        return reviewService.getReviewById(reviewId);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable("id") int reviewId,
                        @PathVariable("userId") int userId) {
        reviewService.addLike(reviewId, userId);
    }

    @PutMapping("{id}/dislike/{userId}")
    public void addDisike(@PathVariable("id") int reviewId,
                          @PathVariable("userId") int userId) {
        reviewService.addDislike(reviewId, userId);
    }


    @DeleteMapping("{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") int reviewId,
                           @PathVariable("userId") int userId) {
        reviewService.deleteLike(reviewId, userId);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable("id") int reviewId,
                              @PathVariable("userId") int userId) {
        reviewService.deleteDislike(reviewId, userId);
    }

}
