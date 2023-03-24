package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.InMemoryUserManager;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {
    private static User user;
    private static UserController userController;

    @BeforeEach
    public void beforeEach() {
        userController = new UserController(new InMemoryUserManager());
        user = User.builder()
                .email("example@email.ru")
                .login("login")
                .name("name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    public void shouldAddUser() {
        userController.createUser(user);
        List<User> expectedUsers = List.of(user);
        List<User> savedUsers = userController.findUsers();
        assertEquals(expectedUsers, savedUsers);
        assertEquals(1, userController.findUsers().size());
    }

    @Test
    public void shouldNotAddUserWhenLoginIsEmpty() {
        user.setLogin(" ");
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(user));
        String expectedMessage = "User login can't be empty or contain spaces";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotAddUserWhenEmailIncorrect() {
        user.setEmail("exampleemail.ru");
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(user));
        String expectedMessage = "User email can't be empty and must contain @";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldNotAddUserWhenBirthdayIncorrect() {
        user.setBirthday(LocalDate.of(2025, 1, 1));
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(user));
        String expectedMessage = "User birthday date is not valid";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    public void shouldAddUserWhenNameIsEmpty() {
        user.setName("");
        userController.createUser(user);
        assertEquals(userController.findUsers().get(0).getName(), userController.findUsers().get(0).getLogin());
    }

    @Test
    public void shouldUpdateUserNormal() {
        userController.createUser(user);

        User updatedUser = user;
        updatedUser.setEmail("new@email.ru");
        updatedUser.setLogin("newLogin");
        updatedUser.setName("newName");
        updatedUser.setBirthday(LocalDate.of(2020, 1, 1));

        userController.updateUser(updatedUser);

        assertEquals(updatedUser.toString(), userController.findUsers().get(0).toString());
    }

    @Test
    public void shouldNotUpdateUserIncorrectId() {
        userController.createUser(user);

        User updatedUser = user;
        updatedUser.setId(999);
        updatedUser.setEmail("new@email.ru");
        updatedUser.setLogin("newLogin");
        updatedUser.setName("newName");
        updatedUser.setBirthday(LocalDate.of(2020, 1, 1));

        ValidationException exception = assertThrows(ValidationException.class, () -> userController.updateUser(updatedUser));
        String expectedMessage = "User with id " + updatedUser.getId() + " doesn't exist";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }
}