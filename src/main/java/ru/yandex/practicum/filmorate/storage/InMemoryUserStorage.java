package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private static int id = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Map<Integer, User> getUsers() {
        return this.users;
    }

    @Override
    public User getUserById(int userId) {
        if (!users.containsKey(userId)) {
            log.error("User with id {} doesn't exist", userId);
            throw new ValidationException("User with id " + userId + " doesn't exist");
        }
        return this.users.get(userId);
    }

    @Override
    public User createUser(User user) {
        if (users.containsKey(user.getId())) {
            log.error("User with id {} already exists", user.getId());
            throw new ValidationException("User with id " + user.getId() + " already exists");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.error("User name empty. Set user name {}", user.getLogin());
            user.setName(user.getLogin());
        }
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
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(int userId) {
        if (!users.containsKey(userId)) {
            log.error("User with id {} doesn't exist", userId);
            throw new ValidationException("User with id " + userId + " doesn't exist");
        }
        users.remove(userId);
    }
}
