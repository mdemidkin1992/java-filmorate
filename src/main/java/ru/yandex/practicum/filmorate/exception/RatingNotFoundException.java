package ru.yandex.practicum.filmorate.exception;

public class RatingNotFoundException extends RuntimeException {
    public RatingNotFoundException(String message) {
        super(message);
    }
}
