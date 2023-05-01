package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@Builder
@EqualsAndHashCode
public class Genre {
    @NotNull
    int id;

    String name;
}