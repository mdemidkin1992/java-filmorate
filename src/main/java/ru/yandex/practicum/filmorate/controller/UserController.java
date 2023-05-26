package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.EventService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;
    private final EventService eventService;

    @Autowired
    public UserController(UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Number of users: \"{}\"", userService.getUsers().size());
        return userService.getUsers();
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable("id") int userId) {
        log.info("GET request received: user with id \"{}\"", userId);
        User response = userService.getUserById(userId);
        log.info("User with id \"{}\": \"{}\"", userId, response.toString());
        return response;
    }

    @PostMapping
    public User createUser(@NotNull @RequestBody @Valid User user) {
        log.info("POST request received: \"{}\"", user);
        User response = userService.createUser(user);
        log.info("Added user: \"{}\"", response.toString());
        return response;
    }

    @PutMapping
    public User updateUser(@NotNull @RequestBody @Valid User user) {
        log.info("PUT request received: \"{}\"", user);
        User response = userService.updateUser(user);
        log.info("Updated user: \"{}\"", response.toString());
        return response;
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") int userId,
                          @PathVariable("friendId") int friendId) {
        log.info("PUT request received: user id \"{}\" adds friend id \"{}\"", userId, friendId);
        userService.addFriend(userId, friendId);
        log.info("User \"{}\" added friend \"{}\"", userId, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") int userId,
                             @PathVariable("friendId") int friendId) {
        log.info("DELETE request received: user id \"{}\" deletes friend id \"{}\"", userId, friendId);
        userService.deleteFriend(userId, friendId);
        log.info("User \"{}\" deleted friend \"{}\"", userId, friendId);
    }

    @GetMapping("{id}/friends")
    public List<User> getUserFriends(@PathVariable("id") int userId) {
        log.info("GET request received: user \"{}\" friends", userId);
        List<User> response = userService.getUserFriends(userId);
        log.info("User \"{}\" friends: \"{}\"", userId, response);
        return response;
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") int userId,
                                       @PathVariable("otherId") int otherId) {
        log.info("GET request received: common friends of users with ids \"{}\" and \"{}\"", userId, otherId);
        List<User> response = userService.getCommonFriends(userId, otherId);
        log.info("Users \"{}\" and \"{}\" common friends: \"{}\"", userId, otherId, response);
        return response;
    }

    @DeleteMapping
    public void deleteUser(@NotNull @RequestBody User user) {

    }

    @GetMapping("{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable("id") int userId) {
        log.info("GET request received: user \"{}\" film recommendations", userId);
        List<Film> response = userService.getFilmRecommendations(userId);
        log.info("User \"{}\" film recommendations: \"{}\"", userId, response);
        return response;
    }

    @GetMapping("{id}/feed")
    public List<Event> getUserFeed(@PathVariable("id") int userId) {
        log.info("GET request received: get feed of user \"{}\"", userId);
        return eventService.getUserEvents(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable("userId") int userId) {
        log.info("DELETE request received: delete user by id \"{}\"",userId);
        userService.deleteUserById(userId);
        log.info("User with id \"{}\" deleted", userId);
    }

}
