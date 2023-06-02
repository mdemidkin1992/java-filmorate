package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.db.UserDbStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.CustomEasyRandom.nextUser;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {

    @Autowired
    private final UserDbStorage userDbStorage;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private static final Validator VALIDATOR;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = validatorFactory.usingContext().getValidator();
    }

    @AfterEach
    public void clearDb() {
        userDbStorage.clearTablesAndResetIds();
    }

    @Test
    public void testValidations() {
        User invalidUser = User.builder().email("wrong email.com@").name("wrong user name").birthday(LocalDate.of(2030, 1, 1)).build();
        Set<ConstraintViolation<User>> validates = VALIDATOR.validate(invalidUser);
        assertTrue(validates.size() > 0);
        validates.stream().map(v -> v.getMessage()).forEach(System.out::println);
    }

    @Test
    public void shouldCreateUser() throws Exception{
        User user1 = nextUser();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value(user1.getName()))
                .andExpect(jsonPath("$.login").value(user1.getLogin()));
    }

    @Test
    public void shouldGetUserByIdWhenIdIsCorrect() throws Exception {
        User user1 = nextUser();

        MvcResult creationResult1 = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        User createdUser1 =
                objectMapper.readValue(creationResult1.getResponse().getContentAsString(), User.class);

        mockMvc.perform(get("/users/{id}", createdUser1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdUser1.getId()))
                .andExpect(jsonPath("$.name").value(createdUser1.getName()));
    }


    @Test
    public void shouldGetAllUsers() throws Exception {
        User user1 = nextUser();
        User user2 = nextUser();

        MvcResult creationResult1 = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user1)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        User createdUser1 =
                objectMapper.readValue(creationResult1.getResponse().getContentAsString(), User.class);

        MvcResult creationResult2 = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        User createdUser2 =
                objectMapper.readValue(creationResult2.getResponse().getContentAsString(), User.class);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("[0].id").value(createdUser1.getId()))
                .andExpect(jsonPath("[0].name").value(createdUser1.getName()))
                .andExpect(jsonPath("[1].id").value(createdUser2.getId()))
                .andExpect(jsonPath("[1].name").value(createdUser2.getName()));
    }

    @Test
    public void shouldAddFriendWhenIdIsCorrect() throws Exception {
        User user1 = nextUser();
        User user2 = nextUser();
        User user3 = nextUser();
        User user4 = nextUser();
        User user5 = nextUser();

        MvcResult creationResult1 = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user1))).andReturn();
        MvcResult creationResult2 = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user2))).andReturn();
        MvcResult creationResult3 = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user3))).andReturn();
        MvcResult creationResult4 = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user4))).andReturn();
        MvcResult creationResult5 = mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(user5))).andReturn();

        User createdUser1 = objectMapper.readValue(creationResult1.getResponse().getContentAsString(), User.class);
        User createdUser2 = objectMapper.readValue(creationResult2.getResponse().getContentAsString(), User.class);
        User createdFriend1 = objectMapper.readValue(creationResult3.getResponse().getContentAsString(), User.class);
        User createdFriend2 = objectMapper.readValue(creationResult4.getResponse().getContentAsString(), User.class);
        User createdFriend3 = objectMapper.readValue(creationResult5.getResponse().getContentAsString(), User.class);

        int userId1 = createdUser1.getId(), userId2 = createdUser2.getId();
        int friendId1 = createdFriend1.getId(), friendId2 = createdFriend2.getId(), friendId3 = createdFriend3.getId();

        mockMvc.perform(put("/users/{id}/friends/{friendId}", userId1, friendId1)).andExpect(status().isOk());
        mockMvc.perform(put("/users/{id}/friends/{friendId}", userId1, friendId2)).andExpect(status().isOk());
        mockMvc.perform(put("/users/{id}/friends/{friendId}", userId1, friendId3)).andExpect(status().isOk());
        mockMvc.perform(put("/users/{id}/friends/{friendId}", userId2, friendId1)).andExpect(status().isOk());
        mockMvc.perform(put("/users/{id}/friends/{friendId}", userId2, friendId2)).andExpect(status().isOk());

        mockMvc.perform(get("/users/{id}/friends/common/{otherId}", userId1, userId2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("[0].id").value(createdFriend2.getId()))
                .andExpect(jsonPath("[0].name").value(createdFriend2.getName()))
                .andExpect(jsonPath("[1].id").value(createdFriend1.getId()))
                .andExpect(jsonPath("[1].name").value(createdFriend1.getName()));

        mockMvc.perform(get("/users/{id}/friends/", userId2)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("[0].id").value(createdFriend1.getId()))
                .andExpect(jsonPath("[0].name").value(createdFriend1.getName()))
                .andExpect(jsonPath("[1].id").value(createdFriend2.getId()))
                .andExpect(jsonPath("[1].name").value(createdFriend2.getName()));
    }

    @Test
    public void shouldNotRemoveFriendsWhenFriendIdIsIncorrect() {
        User user1 = User.builder().name("Mark").login("marklogin").email("mark@email.com").birthday(LocalDate.of(1992, 1, 2)).build();
        User user2 = User.builder().name("Ben").login("benlogin").email("ben@email.com").birthday(LocalDate.of(1995, 2, 4)).build();

        userDbStorage.createUser(user1);
        userDbStorage.createUser(user2);

        int userId = user1.getId();
        int friendId = 333;
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userDbStorage.deleteFriend(userId, friendId));
        String expectedMessage = "User with id 333 doesn't exist";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotRemoveFriendsWhenUserIdIsIncorrect() {
        User user1 = User.builder().name("Mark").login("marklogin").email("mark@email.com").birthday(LocalDate.of(1992, 1, 2)).build();
        User user2 = User.builder().name("Ben").login("benlogin").email("ben@email.com").birthday(LocalDate.of(1995, 2, 4)).build();

        userDbStorage.createUser(user1);
        userDbStorage.createUser(user2);

        int userId = 111;
        int friendId = user2.getId();
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userDbStorage.deleteFriend(userId, friendId));
        String expectedMessage = "User with id 111 doesn't exist";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldAddUserWhenNameIsEmpty() {
        User user6 = User.builder().email("example_6@email.ru").login("login_6").birthday(LocalDate.of(1994, 5, 14)).build();
        userDbStorage.createUser(user6);
        assertEquals(userDbStorage.getUserById(user6.getId()).getName(), userDbStorage.getUserById(user6.getId()).getLogin());
    }

    @Test
    public void shouldUpdateUserNormal() {
        User user1 = User.builder().name("Mark").login("marklogin").email("mark@email.com").birthday(LocalDate.of(1992, 1, 2)).build();
        userDbStorage.createUser(user1);

        int userId = user1.getId();
        final User updatedUser = userDbStorage.getUserById(userId);
        updatedUser.setEmail("new@email.ru");
        updatedUser.setLogin("newLogin");
        updatedUser.setName("newName");
        updatedUser.setBirthday(LocalDate.of(2020, 1, 1));

        userDbStorage.updateUser(updatedUser);

        User expected = updatedUser;
        User actual = userDbStorage.getUserById(user1.getId());

        assertEquals(expected, actual);
    }

    @Test
    public void shouldNotUpdateUserIncorrectId() {
        User user1 = User.builder().name("Mark").login("marklogin").email("mark@email.com").birthday(LocalDate.of(1992, 1, 2)).build();
        userDbStorage.createUser(user1);
        int userId = user1.getId();
        final User updatedUser = userDbStorage.getUserById(userId);
        updatedUser.setId(999);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userDbStorage.updateUser(updatedUser));
        String expectedMessage = "User with id " + updatedUser.getId() + " doesn't exist";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldGetUserWhenIdIsCorrect() {
        User user1 = User.builder().name("Mark").login("marklogin").email("mark@email.com").birthday(LocalDate.of(1992, 1, 2)).build();
        userDbStorage.createUser(user1);
        int userId = user1.getId();
        User actualUser = userDbStorage.getUserById(userId);
        assertEquals(userId, actualUser.getId());
    }

    @Test
    public void shouldNotGetUserWhenIdIsIncorrect() {
        User user1 = User.builder().name("Mark").login("marklogin").email("mark@email.com").birthday(LocalDate.of(1992, 1, 2)).build();
        userDbStorage.createUser(user1);
        int userId = 999;
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userDbStorage.getUserById(userId));
        String expectedMessage = "User with id " + userId + " doesn't exist";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

}