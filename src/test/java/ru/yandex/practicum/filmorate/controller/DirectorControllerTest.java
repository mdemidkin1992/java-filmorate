package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.impl.db.DirectorDbStorage;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DirectorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    DirectorDbStorage directorDbStorage;

    @AfterEach
    void clearTableAndRestartId() {
        directorDbStorage.clearTableAndResetId();
    }

    @Test
    void shouldCreateDirector() throws Exception {
        Director director = Director.builder().name("Quentin Tarantino").build();

        mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Quentin Tarantino"))
                .andReturn();
    }

    @Test
    void shouldGetDirectors() throws Exception {
        Director director = Director.builder().name("Quentin Tarantino").build();

        mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(get("/directors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Quentin Tarantino"));
    }

    @Test
    void shouldGetDirectorById() throws Exception {
        Director director = Director.builder().name("Quentin Tarantino").build();

        MvcResult creationResult = mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Director createdDirector =
                objectMapper.readValue(creationResult.getResponse().getContentAsString(), Director.class);

        mockMvc.perform(get("/directors/{id}", createdDirector.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdDirector.getId()))
                .andExpect(jsonPath("$.name").value(createdDirector.getName()));
    }

    @Test
    void shouldNotCreateDirectorsWithBadContent() throws Exception {
        Director director = Director.builder().name(" ").build();

        mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andExpect(status().isInternalServerError());

        director = Director.builder().build();

        mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void shouldUpdateDirector() throws Exception {
        Director director = Director.builder().name("Steven Allan Spielberg").build();

        MvcResult creationResult = mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Director createdDirector =
                objectMapper.readValue(creationResult.getResponse().getContentAsString(), Director.class);

        createdDirector.setName("Steven Spielberg");

        mockMvc.perform(put("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdDirector)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Steven Spielberg"));
    }

    @Test
    public void shouldNotUpdateDirector() throws Exception {
        Director director = Director.builder().name("Steven Allan Spielberg").build();

        mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andDo(print())
                .andExpect(status().isOk());

        Director directorWithBadId = Director.builder().id(666).name("Steven Allan Spielberg").build();

        mockMvc.perform(put("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(directorWithBadId)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldRemoveDirector() throws Exception {
        Director director = Director.builder().name("Steven Allan Spielberg").build();

        MvcResult creationResult = mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Director createdDirector =
                objectMapper.readValue(creationResult.getResponse().getContentAsString(), Director.class);

        mockMvc.perform(delete("/directors/{id}", createdDirector.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/directors/{id}", createdDirector.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}