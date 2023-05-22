package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class Review {
    static final int MAX_REVIEW_LENGTH = 4096;

    @NotNull
    int reviewId;

    @NotNull
    Integer filmId;

    @NotNull
    Integer userId;

    @Size(max = MAX_REVIEW_LENGTH, message = "Max film review length " + MAX_REVIEW_LENGTH)
    @NotNull
    @NotBlank(message = "Content of the review can't be empty")
    String content;

    @NotNull
    @JsonProperty("isPositive")
    Boolean isPositive;

    int useful;

}
