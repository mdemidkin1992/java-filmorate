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
        log.info("GET request for filmId=\"{}\" count=\"{}\" reviews received", filmId, count);
        List<Review> response = reviewService.getReviews(filmId, count);
        log.info("Number of reviews: {}", response.size());
        log.info("All reviews: {}", response);
        return response;
    }

    @PostMapping
    public Review createReview(@NotNull @Valid @RequestBody Review review) {
        log.info("POST request received: {}", review);
        Review response = reviewService.createReview(review);
        log.info("Added review: {}", response.toString());
        return response;
    }

    @DeleteMapping("{id}")
    public void deleteReview(@PathVariable("id") int reviewId) {
        log.info("DELETE request received: review with id \"{}\"", reviewId);
        reviewService.deleteReviewById(reviewId);
        log.info("Review with id \"{}\" deleted", reviewId);
    }

    @PutMapping
    public Review updateReview(@NotNull @Valid @RequestBody Review review) {
        log.info("PUT request received: {}", review);
        Review response = reviewService.updateReview(review);
        log.info("Updated review: {}", response.toString());
        return response;
    }

    @GetMapping("{id}")
    public Review getReviewById(@PathVariable("id") int reviewId) {
        log.info("GET request received: review with id \"{}\"", reviewId);
        Review response = reviewService.getReviewById(reviewId);
        log.info("Review with id \"{}\" : {}", reviewId, response.toString());
        return response;
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable("id") int reviewId,
                        @PathVariable("userId") int userId) {
        log.info("PUT request received: review id \"{}\" likes user id \"{}\"", reviewId, userId);
        reviewService.addLike(reviewId, userId);
        log.info("Review id: \"{}\" added like from user \"{}\"", reviewId, userId);
    }

    @PutMapping("{id}/dislike/{userId}")
    public void addDisike(@PathVariable("id") int reviewId,
                          @PathVariable("userId") int userId) {
        log.info("PUT request received: review id \"{}\" dislikes user id \"{}\"", reviewId, userId);
        reviewService.addDislike(reviewId, userId);
        log.info("Review id: \"{}\" added dislike from user \"{}\"", reviewId, userId);
    }


    @DeleteMapping("{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") int reviewId,
                           @PathVariable("userId") int userId) {
        log.info("DELETE request received: review id \"{}\" deletes like from review id \"{}\"", reviewId, userId);
        reviewService.deleteLike(reviewId, userId);
        log.info("Review id: \"{}\" deleted like from user \"{}\"", reviewId, userId);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable("id") int reviewId,
                              @PathVariable("userId") int userId) {
        log.info("DELETE request received: review id \"{}\" deletes dislike from review id \"{}\"", reviewId, userId);
        reviewService.deleteDislike(reviewId, userId);
        log.info("Review id: \"{}\" deleted dislike from user \"{}\"", reviewId, userId);
    }

}
