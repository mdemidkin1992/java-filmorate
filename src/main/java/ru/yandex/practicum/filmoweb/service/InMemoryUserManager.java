package ru.yandex.practicum.filmoweb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import ru.yandex.practicum.filmoweb.exception.ValidationException;
import ru.yandex.practicum.filmoweb.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class InMemoryUserManager implements UserManager {
    private static int id = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User createUser(User user) {
        validateUser(user);
        user.setId(++id);
        users.put(id, user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            log.error("User with id {} doesn't exist", user.getId());
            throw new ValidationException("User with id " + user.getId() + " doesn't exist");
        }
        validateUser(user);
        users.put(user.getId(), user);
        return user;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("User email can't be empty and must contain @");
            throw new ValidationException("User email can't be empty and must contain @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("User login can't be empty or contain spaces");
            throw new ValidationException("User login can't be empty or contain spaces");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.error("User name empty. Set user name {}", user.getLogin());
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.error("User birthday date is not valid");
            throw new ValidationException("User birthday date is not valid");
        }
    }
}
