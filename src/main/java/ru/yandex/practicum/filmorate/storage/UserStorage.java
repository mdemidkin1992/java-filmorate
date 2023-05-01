package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    User getUserById(int userId);

    List<User> getFriends(String userId);

    List<User> getUsers();

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getCommonFriends(int userId, int otherId);
}
