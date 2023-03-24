package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.model.validator.ReleaseDateConstraint;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    static final int MIN_DESCRIPTION_LENGTH = 200;

    @NotNull
    int id;

    @NotNull
    @NotBlank(message = "Film name can't be empty")
    String name;

    @Size(max = MIN_DESCRIPTION_LENGTH, message = "Max film description length " + MIN_DESCRIPTION_LENGTH)
    String description;

    @NotNull
    @ReleaseDateConstraint(message = "Film release date should be after 28 December 1895")
    LocalDate releaseDate;

    @NotNull
    @Positive(message = "Film duration should be > 0")
    int duration;
}