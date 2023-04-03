package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> findUsers() {
        log.info("Number of users: {}", userService.getUsers().size());
        return userService.getUsers();
    }

    @GetMapping("{id}")
    public User getUserById(@PathVariable("id") Integer userId) {
        log.info("GET request received: user with id \"{}\"", userId);
        User response = userService.getUserById(userId);
        log.info("User with id \"{}\"", response.toString());
        return response;
    }

    @PostMapping
    public User createUser(@NotNull @RequestBody @Valid User user) {
        log.info("POST request received: {}", user);
        User response = userService.createUser(user);
        log.info("Added user: {}", response.toString());
        return response;
    }

    @PutMapping
    public User updateUser(@NotNull @RequestBody @Valid User user) {
        log.info("PUT request received: {}", user);
        User response = userService.updateUser(user);
        log.info("Updated user: {}", response.toString());
        return response;
    }

    @PutMapping("{id}/friends/{friendId}")
    public Set<Long> addFriend(@PathVariable("id") int userId,
                               @PathVariable("friendId") int friendId) {
        log.info("PUT request received: user id \"{}\" adds friend id \"{}\"", userId, friendId);
        Set<Long> response = userService.addFriend(userId, friendId);
        log.info("User {} updated friends list: {}", userId, response);
        return response;
    }

    @DeleteMapping("{id}/friends/{friendId}")
    public Set<Long> deleteFriend(@PathVariable("id") int userId,
                                  @PathVariable("friendId") int friendId) {
        log.info("DELETE request received: user id \"{}\" deletes friend id \"{}\"", userId, friendId);
        Set<Long> response = userService.deleteFriend(userId, friendId);
        log.info("User {} updated friends list: {}", userId, response);
        return response;
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

    // TODO Обновление эндпоинтов — UserController:
    /*
    1. GET /users/{id} — с помощью @PathVariable получать по id пользователя. DONE
    2. PUT /users/{id}/friends/{friendId} — добавление в друзья. DONE
    3. DELETE /users/{id}/friends/{friendId} — удаление из друзей. DONE
    4. GET /users/{id}/friends — возвращаем список пользователей, являющихся его друзьями. DONE
    5. GET /users/{id}/friends/common/{otherId} — список друзей, общих с другим пользователем. DONE
    */
}
