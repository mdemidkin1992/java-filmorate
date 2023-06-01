package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class UserDbStorageTest {

    private final UserDbStorage userDbStorage;

    @Test
    void shouldBeSize0CorrectDeleteUser() {
        User user = createUser();
        userDbStorage.createUser(user);
        userDbStorage.deleteUserById(userDbStorage.getUsers().stream().findFirst().get().getId());
        assertEquals(userDbStorage.getUsers().size(), 0);
    }

    @Test
    void shouldThrowExceptionIncorrectDeleteUser() {
        assertThrows(UserNotFoundException.class,
                () -> userDbStorage.deleteUserById(1));
    }

    private User createUser() {
        return User.builder().name("Ivan").login("ivanivan").email("ivan@yandex.ru")
                .birthday(LocalDate.of(2000,10,10)).build();
    }

    @AfterEach
    public void clearDb() {
        userDbStorage.clearTablesAndResetIds();
    }
}
