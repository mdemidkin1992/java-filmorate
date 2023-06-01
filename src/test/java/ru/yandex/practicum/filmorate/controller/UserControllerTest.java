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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static util.CustomEasyRandom.nextUser;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    private final UserDbStorage userDbStorage;
    private static final Validator VALIDATOR;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        VALIDATOR = validatorFactory.usingContext().getValidator();
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
        MvcResult result = this.mockMvc.perform(get("/users/{id}/friends/common/{toherId}", userId1, userId2)
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

    @AfterEach
    public void clearDb() {
        userDbStorage.clearDb();
    }

    @Test
    public void testValidations() {
        User invalidUser = nextUser("wrong email.com@");
        Set<ConstraintViolation<User>> validates = VALIDATOR.validate(invalidUser);
        assertTrue(validates.size() > 0);
        validates.stream().map(v -> v.getMessage()).forEach(System.out::println);
    }

    @Test
    public void shouldGetAllUsers() throws Exception {
        createUser(nextUser("mark@email.com"));
        createUser(nextUser("ben@email.com"));
        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)))
                .andReturn();
    }

    @Test
    public void shouldAddFriendWhenIdIsCorrect() throws Exception {
        User user1 = createUser(nextUser("mark@email.com"));
        User user2 = createUser(nextUser("ben@email.com"));
        User user4 = createUser(nextUser("ben@email.com"));
        User user5 = createUser(nextUser("ben@email.com"));

        int userId1 = user1.getId();
        int userId2 = user2.getId();
        int friendId2 = user4.getId();
        int friendId3 = user5.getId();

        addFriend(userId1, friendId2);
        addFriend(userId1, friendId3);

        addFriend(userId2, friendId2);
        addFriend(userId2, friendId3);

        List<User> expectedUser1FriendList = new LinkedList<>();
        expectedUser1FriendList.add(getUserById(friendId2));
        expectedUser1FriendList.add(getUserById(friendId3));

        List<User> actualUser1FriendsList = getFriends(userId1);
        assertEquals(expectedUser1FriendList, actualUser1FriendsList);

        List<User> expectedCommonFriends = new LinkedList<>();
        expectedCommonFriends.add(getUserById(friendId3));
        expectedCommonFriends.add(getUserById(friendId2));

        List<User> actualCommonFriends = getCommonFriends(userId1, userId2);

        assertEquals(expectedCommonFriends.size(), actualCommonFriends.size());
    }

    @Test
    public void shouldNotRemoveFriendsWhenFriendIdIsIncorrect() throws Exception {
        User user1 = createUser(nextUser("mark@email.com"));
        createUser(nextUser("ben@email.com"));
        int friendId = 333;
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userDbStorage.deleteFriend(user1.getId(), friendId));
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