package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.validator.ReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class Film {
    static final int MAX_DESCRIPTION_LENGTH = 200;
    int id;

    @NotNull
    @NotBlank(message = "Film name can't be empty")
    String name;

    @Size(max = MAX_DESCRIPTION_LENGTH, message = "Max film description length " + MAX_DESCRIPTION_LENGTH)
    String description;

    @NotNull
    @ReleaseDateConstraint(message = "Film release date should be after 28 December 1895")
    LocalDate releaseDate;

    @NotNull
    @Positive(message = "Film duration should be > 0")
    int duration;

    List<Genre> genres;

    Rating mpa;

}