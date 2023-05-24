package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.EventNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;

import java.util.List;

public interface EventStorage {
    Event createEvent(Event event);

    List<Event> getUserEvents(int userId);

    Event getEvent(int entityId, EventType eventType, OperationType operationType) throws EventNotFoundException;
}
