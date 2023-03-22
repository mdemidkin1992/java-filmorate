package ru.yandex.practicum.filmoweb.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmoweb.model.User;
import ru.yandex.practicum.filmoweb.service.UserManager;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserManager manager;

    public UserController(@Autowired UserManager manager) {
        this.manager = manager;
    }

    @GetMapping
    public List<User> findUsers() {
        log.info("Number of users: {}", manager.getUsers().size());
        return manager.getUsers();
    }

    @PostMapping
    public User createUser(@NotNull @RequestBody User user) {
        log.info("POST request received: {}", user);
        User response = manager.createUser(user);
        log.info("Added user: {}", user.toString());
        return response;
    }

    @PutMapping
    public User updateUser(@NotNull @RequestBody User user) {
        log.info("PUT request received: {}", user);
        User response = manager.updateUser(user);
        log.info("Updated user: {}", user.toString());
        return response;
    }
}
