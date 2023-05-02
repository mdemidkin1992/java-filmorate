package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.validator.BirthdayConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class User {
    int id;

    @NotNull
    @NotBlank(message = "User email can't be empty")
    @Email(message = "User email is of invalid format")
    String email;

    @NotNull
    @NotBlank(message = "User login can't be empty")
    @Pattern(regexp = "^[\\S]*$", message = "User login can't contain spaces")
    String login;

    String name;

    @NotNull
    @BirthdayConstraint(message = "User birthday date can't be in the future")
    LocalDate birthday;

    private final Set<Long> friends = new HashSet<>();

    public void addFriend(int friendId) {
        friends.add((long) friendId);
    }

    public void deleteFriend(int friendId) {
        friends.remove((long) friendId);
    }
}