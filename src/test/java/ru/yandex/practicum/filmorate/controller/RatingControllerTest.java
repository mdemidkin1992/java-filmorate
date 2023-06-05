package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.RatingNotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
class RatingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void shouldGetAllRatings() throws Exception {
        mockMvc.perform(get("/mpa")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("[0].id").value(1))
                .andExpect(jsonPath("[0].name").value("G"))
                .andExpect(jsonPath("[1].id").value(2))
                .andExpect(jsonPath("[1].name").value("PG"))
                .andExpect(jsonPath("[2].id").value(3))
                .andExpect(jsonPath("[2].name").value("PG-13"))
                .andExpect(jsonPath("[3].id").value(4))
                .andExpect(jsonPath("[3].name").value("R"))
                .andExpect(jsonPath("[4].id").value(5))
                .andExpect(jsonPath("[4].name").value("NC-17"));
    }

    @Test
    public void shouldGetRatingById() throws Exception {
        Rating rating = Rating.builder().id(1).name("G").build();

        mockMvc.perform(get("/mpa/{id}", rating.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rating.getId()))
                .andExpect(jsonPath("$.name").value(rating.getName()));
    }

    @Test
    public void shouldNotGetRatingWhenIdIsIncorrect() throws Exception {
        int ratingId = 111;

        mockMvc.perform(get("/mpa/{id}", ratingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(result -> assertTrue(result.getResolvedException()
                        instanceof RatingNotFoundException));
    }

}