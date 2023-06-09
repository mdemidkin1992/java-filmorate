package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@Slf4j
public class RatingController {
    private final FilmService filmService;

    @Autowired
    public RatingController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Rating> getRatings() {
        log.info("Number of ratings: {}", filmService.getRatings().size());
        return filmService.getRatings();
    }

    @GetMapping("{id}")
    public Rating getRatingById(@PathVariable("id") int ratingId) {
        log.info("GET request received: rating with id \"{}\"", ratingId);
        Rating response = filmService.getRatingById(ratingId);
        log.info("Rating with id \"{}\" : {}", ratingId, response.toString());
        return response;
    }
}
