package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class SimpleCrudOperations {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    public Film createFilm(Film film) throws Exception {
        MvcResult result = this.mockMvc.perform(post("/films")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(film)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(film.getName()))
                .andReturn();
        return objectMapper.readValue(
                result.getResponse().getContentAsString(),
                Film.class
        );
    }

    public void addScore(int userId, int filmId, int score) throws Exception {
        mockMvc.perform(
                        put("/films/{id}/score/{userId}", userId, filmId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("score", String.valueOf(score)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    public void deleteScore(int userId, int filmId) throws Exception {
        mockMvc.perform(
                        delete("/films/{id}/score/{userId}", userId, filmId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    public Film getFilmById(int filmId) throws Exception {
        MvcResult result = this.mockMvc.perform(get("/films/{id}", filmId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readValue(
                result.getResponse().getContentAsString(),
                Film.class
        );
    }

    public List<Film> getCommonFilms(int userId, int friendId) throws Exception {
        MvcResult result = this.mockMvc.perform(get("/films/common")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", String.valueOf(userId))
                        .param("friendId", String.valueOf(friendId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        return List.of(objectMapper.readValue(
                result.getResponse().getContentAsString(),
                Film[].class
        ));
    }

    public User createUser(User user) throws Exception {
        MvcResult result = this.mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andReturn();
        return objectMapper.readValue(
                result.getResponse().getContentAsString(),
                User.class
        );

    }

    public void addFriend(int userId, int friendId) throws Exception {
        mockMvc.perform(
                        put("/users/{id}/friends/{friendId}", userId, friendId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    public void deleteFriend(int userId, int friendId) throws Exception {
        mockMvc.perform(
                        delete("/users/{id}/friends/{friendId}", userId, friendId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    public List<User> getFriends(int userId) throws Exception {
        MvcResult result = this.mockMvc.perform(get("/users/{id}/friends", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        return List.of(objectMapper.readValue(
                result.getResponse().getContentAsString(),
                User[].class
        ));
    }

    public List<User> getCommonFriends(int userId1, int userId2) throws Exception {
        MvcResult result = this.mockMvc.perform(get("/users/{id}/friends/common/{otherId}", userId1, userId2)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        return List.of(objectMapper.readValue(
                result.getResponse().getContentAsString(),
                User[].class
        ));
    }

    public User getUserById(int userId) throws Exception {
        MvcResult result = this.mockMvc.perform(get("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readValue(
                result.getResponse().getContentAsString(),
                User.class
        );
    }
}
