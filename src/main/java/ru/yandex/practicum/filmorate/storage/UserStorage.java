package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User createUser(User user);

    User updateUser(User user);

    User getUserById(int userId);

    List<User> getFriends(int userId);

    List<User> getUsers();

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getCommonFriends(int userId, int otherId);

    int getOtherUserIdWithCommonInterests(int userId);

    void deleteUserById(int userId);


}
