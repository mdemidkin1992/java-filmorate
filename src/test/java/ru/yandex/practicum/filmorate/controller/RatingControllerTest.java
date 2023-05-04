package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RatingControllerTest {
    private final RatingController ratingController;

    @Test
    public void shouldGetAllRatings() {
        List<Rating> actualratings = ratingController.getRatings();
        List<Rating> expectedRatings = new LinkedList<>();
        expectedRatings.add(Rating.builder().id(1).name("G").build());
        expectedRatings.add(Rating.builder().id(2).name("PG").build());
        expectedRatings.add(Rating.builder().id(3).name("PG-13").build());
        expectedRatings.add(Rating.builder().id(4).name("R").build());
        expectedRatings.add(Rating.builder().id(5).name("NC-17").build());

        Assertions.assertEquals(expectedRatings, actualratings, "Списки рейтингов не совпадают");
    }

    @Test
    public void shouldGetRatingById() {
        int ratingId = 1;
        Rating expectedRating = Rating.builder().id(ratingId).name("G").build();
        Rating actualRating = ratingController.getRatingById(ratingId);

        Assertions.assertEquals(expectedRating, actualRating, "Рейтинги не сопадают");
    }

    @Test
    public void shouldNotGetRatingWhenIdIsIncorrect() {
        int ratingId = 111;
        RatingNotFoundException exception = assertThrows(RatingNotFoundException.class, () -> ratingController.getRatingById(ratingId));
        String expectedMessage = "Rating with id " + ratingId + " doesn't exist";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}