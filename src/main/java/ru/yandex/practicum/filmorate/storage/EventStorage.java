package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    Event createEvent(Event event);

    List<Event> getUserEvents(int userId);
}
