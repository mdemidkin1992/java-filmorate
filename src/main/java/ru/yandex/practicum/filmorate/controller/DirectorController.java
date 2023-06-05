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
        return directorService.getDirectors();
    }

    @PostMapping
    public Director createDirector(@NotNull @RequestBody @Valid Director director) {
        return directorService.createDirector(director);
    }

    @GetMapping("{id}")
    public Director getDirectorById(@PathVariable("id") int directorId) {
        return directorService.getDirectorById(directorId);
    }

    @PutMapping
    public Director updateDirector(@NotNull @RequestBody @Valid Director director) {
        return directorService.updateDirector(director);
    }

    @DeleteMapping("{id}")
    public boolean deleteDirectorById(@PathVariable("id") int directorId) {
        return directorService.deleteDirector(directorId);
    }
}