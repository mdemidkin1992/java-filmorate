package ru.yandex.practicum.filmoweb.service;

import ru.yandex.practicum.filmoweb.model.User;

import java.util.List;

public interface UserManager {
    User createUser(User user);

    User updateUser(User user);

    List<User> getUsers();
}
