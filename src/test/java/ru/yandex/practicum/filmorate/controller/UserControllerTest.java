package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.InMemoryUserManager;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
    private static User user;
    private static UserController userController;
    private final static Validator validator;

    static {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.usingContext().getValidator();
    }

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
    public void testValidations() {
        User invalidUser = userController.createUser(user);
        invalidUser.setEmail("wrong email.com@");
        invalidUser.setLogin(null);
        invalidUser.setName("wrong user name");
        invalidUser.setBirthday(LocalDate.of(2030, 1, 1));

        Set<ConstraintViolation<User>> validates = validator.validate(invalidUser);
        assertTrue(validates.size() > 0);
        validates.stream().map(v -> v.getMessage()).forEach(System.out::println);
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
    public void shouldNotAddUserWhenUserAlreadyExists() {
        User newUser = userController.createUser(user);
        ValidationException exception = assertThrows(ValidationException.class, () -> userController.createUser(newUser));
        String expectedMessage = "User with id " + newUser.getId() + " already exists";
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