package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/directors")
@Slf4j
public class DirectorController {

    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public List<Director> getDirectors() {
        log.info("GET request for all directors received");
        List<Director> response = directorService.getDirectors();
        log.info("Number of directors: {}", directorService.getDirectors().size());
        log.info("All directors: {}", response);
        return response;
    }

    @PostMapping
    public Director createDirector(@NotNull @RequestBody @Valid Director director) {
        log.info("POST request received: {}", director);
        Director response = directorService.createDirector(director);
        log.info("Added director: {}", response.toString());
        return response;
    }

    @GetMapping("{id}")
    public Director getDirectorById(@PathVariable("id") int directorId) {
        log.info("GET request received: director with id \"{}\"", directorId);
        Director response = directorService.getDirectorById(directorId);
        log.info("Director with id \"{}\" : {}", directorId, response.toString());
        return response;
    }

    @PutMapping
    public Director updateDirector(@NotNull @RequestBody @Valid Director director) {
        log.info("PUT request received: {}", director);
        Director response = directorService.updateDirector(director);
        log.info("Updated director: {}", response.toString());
        return response;
    }

    @DeleteMapping("{id}")
    public boolean deleteDirectorById(@PathVariable("id") int directorId) {
        log.info("GET request for deleting director with id \"{}\" received", directorId);
        return directorService.deleteDirector(directorId);
    }
}