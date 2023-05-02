package ru.yandex.practicum.filmorate.storage.impl.mem;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Component("inMemoryUserStorage")
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private static int id = 0;
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(this.users.values());
    }

    @Override
    public void addFriend(int userId, int friendId) {

    }

    @Override
    public void deleteFriend(int userId, int friendId) {

    }

    @Override
    public List<User> getCommonFriends(int userId, int otherId) {
        return null;
    }


    @Override
    public User getUserById(int userId) {
        if (!users.containsKey(userId)) {
            log.error("User with id {} doesn't exist", userId);
            throw new UserNotFoundException("User with id " + userId + " doesn't exist");
        }
        return this.users.get(userId);
    }

    @Override
    public List<User> getFriends(int userId) {
        return null;
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
            throw new UserNotFoundException("User with id " + user.getId() + " doesn't exist");
        }
        users.put(user.getId(), user);
        return user;
    }
}
