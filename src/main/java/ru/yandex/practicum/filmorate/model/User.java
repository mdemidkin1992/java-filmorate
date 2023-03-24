package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.model.validator.BirthdayConstraint;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @NotNull
    int id;

    @NotNull
    @NotBlank(message = "User email can't be empty")
    @Email(message = "User email is of invalid format")
    String email;

    @NotNull
    @NotBlank(message = "User login can't be empty")
    @Pattern(regexp = "^[\\S]*$", message = "User name can't contain spaces")
    String login;

    String name;

    @NotNull
    @BirthdayConstraint(message = "User birthday date can't be in the past")
    LocalDate birthday;
}