package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Map;

@Data
@Builder
public class Director {

    int id;
    @NotBlank
    String name;

    public Map<String, Object> toMap() {
        return Map.of("DIRECTOR_NAME", name);
    }
}