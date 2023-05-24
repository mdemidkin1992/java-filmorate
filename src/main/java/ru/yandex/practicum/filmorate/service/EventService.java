package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
public class EventService {
    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    public EventService(
            @Qualifier("eventDbStorage")
            EventStorage eventStorage,
            @Qualifier("userDbStorage")
            UserStorage userStorage
    ) {
        this.eventStorage = eventStorage;
        this.userStorage = userStorage;
    }

    public List<Event> getUserEvents(int userId) throws UserNotFoundException {
        User user = userStorage.getUserById(userId);
        return eventStorage.getUserEvents(user.getId());
    }
}
