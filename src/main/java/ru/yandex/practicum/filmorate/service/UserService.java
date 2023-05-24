package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.aspects.annotation.SaveUserEvent;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
    }

    public User createUser(User user) {
        return userStorage.createUser(user);
    }

    public User updateUser(User user) {
        return userStorage.updateUser(user);
    }

    public User getUserById(int userId) {
        return userStorage.getUserById(userId);
    }

    public List<User> getUsers() {
        return new ArrayList<>(userStorage.getUsers());
    }

    @SaveUserEvent(eventType = EventType.FRIEND, operation = OperationType.ADD, entityIdParamName = "friendId")
    public void addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
    }

    @SaveUserEvent(eventType = EventType.FRIEND, operation = OperationType.REMOVE, entityIdParamName = "friendId")
    public void deleteFriend(int userId, int friendId) {
        userStorage.deleteFriend(userId, friendId);
    }

    public List<User> getUserFriends(int userId) {
        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        return userStorage.getCommonFriends(userId, otherId);
    }

    public List<Film> getFilmRecommendations(int userId) {
        int otherUserId = userStorage.getOtherUserIdWithCommonInterests(userId);
        List<Film> userLikedFilms = filmStorage.getFilmsLikedByUser(userId);
        List<Film> otherUserLikedFilms = filmStorage.getFilmsLikedByUser(otherUserId);

        Set<Film> union = new HashSet<>(userLikedFilms);
        union.addAll(otherUserLikedFilms);
        union.removeAll(userLikedFilms);

        return new ArrayList<>(union);
    }
}
