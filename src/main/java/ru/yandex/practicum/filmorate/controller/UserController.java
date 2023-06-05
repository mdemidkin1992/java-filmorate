package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
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
        return userService.getUsers();
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable("id") int userId) {
        return userService.getUserById(userId);
    }

    @PostMapping
    public User createUser(@NotNull @RequestBody @Valid User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@NotNull @RequestBody @Valid User user) {
        return userService.updateUser(user);
    }

    @PutMapping("{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") int userId,
                          @PathVariable("friendId") int friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable("id") int userId,
                             @PathVariable("friendId") int friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("{id}/friends")
    public List<User> getUserFriends(@PathVariable("id") int userId) {
        return userService.getUserFriends(userId);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable("id") int userId,
                                       @PathVariable("otherId") int otherId) {
        return userService.getCommonFriends(userId, otherId);
    }

    @GetMapping("{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable("id") int userId) {
        return userService.getFilmRecommendations(userId);
    }

    @GetMapping("{id}/feed")
    public List<Event> getUserFeed(@PathVariable("id") int userId) {
        return eventService.getUserEvents(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable("userId") int userId) {
        userService.deleteUserById(userId);
    }

}
