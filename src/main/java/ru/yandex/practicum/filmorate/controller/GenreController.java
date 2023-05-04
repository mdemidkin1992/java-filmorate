package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {
    private final FilmService filmService;

    @Autowired
    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Genre> getGenres() {
        log.info("Number of genres: {}", filmService.getGenres().size());
        return filmService.getGenres();
    }

    @GetMapping("{id}")
    public Genre getGenreById(@PathVariable("id") int genreId) {
        log.info("GET request received: genre with id \"{}\"", genreId);
        Genre response = filmService.getGenreById(genreId);
        log.info("Genre with id \"{}\" : {}", genreId, response.toString());
        return response;
    }
}
