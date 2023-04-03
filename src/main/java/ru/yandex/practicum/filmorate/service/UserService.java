package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.userStorage = inMemoryUserStorage;
    }

    public User createUser(User user) {
        return this.userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return this.userStorage.updateUser(user);
    }

    public void deleteUser(int userId) {
        this.userStorage.deleteUser(userId);
    }

    public User getUserById(int userId) {
        return this.userStorage.getUserById(userId);
    }

    public List<User> getUsers() {
        return new ArrayList<>(this.userStorage.getUsers().values());
    }

    public Set<Long> addFriend(int userId, int friendId) {
        checkUserIds(userId, friendId);
        userStorage.getUserById(userId).addFriend(friendId);
        userStorage.getUserById(friendId).addFriend(userId);
        return userStorage.getUserById(userId).getFriends();
    }

    public Set<Long> deleteFriend(int userId, int friendId) {
        checkUserIds(userId, friendId);
        userStorage.getUserById(userId).deleteFriend(friendId);
        userStorage.getUserById(friendId).deleteFriend(userId);
        return userStorage.getUserById(userId).getFriends();
    }

    public List<User> getUserFriends(int userId) {
        Set<Long> friendsIds = userStorage.getUserById(userId).getFriends();
        return userStorage.getUsers().values().stream()
                .filter(user -> friendsIds.contains((long) user.getId()))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        Set<Long> userFriends = userStorage.getUserById(userId).getFriends();
        Set<Long> otherUserFriends = userStorage.getUserById(otherId).getFriends();

        Set<Long> intersection = new HashSet<>(userFriends);
        intersection.retainAll(otherUserFriends);

        return userStorage.getUsers().values().stream()
                .filter(user -> intersection.contains((long) user.getId()))
                .collect(Collectors.toList());
    }

    private void checkUserIds(int userId, int friendId) {
        if (!userStorage.getUsers().containsKey(userId)) {
            log.error("User with id {} doesn't exist.", userId);
            throw new UserNotFoundException(String.format("User with id \"%s\" doesn't exist.", userId));
        }
        if (!userStorage.getUsers().containsKey(friendId)) {
            log.error("User with id {} doesn't exist.", friendId);
            throw new UserNotFoundException(String.format("User with id \"%s\" doesn't exist.", friendId));
        }
    }
}
