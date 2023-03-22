package ru.yandex.practicum.filmoweb.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(String description) {
        super(description);
    }
}
