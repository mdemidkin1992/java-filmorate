package ru.yandex.practicum.filmoweb.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Film {
    @Builder.Default
    private int id = 0;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
}
