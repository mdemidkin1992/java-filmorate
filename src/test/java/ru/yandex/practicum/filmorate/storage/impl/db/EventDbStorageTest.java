package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.RequiredArgsConstructor;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EventDbStorageTest {

    private final EventDbStorage eventDbStorage;
    private final UserDbStorage userDbStorage;
    private final EasyRandom generator = new EasyRandom();

    private User user;
    private Event event;

    @BeforeEach
    public void beforeEach() {
        user = generator.nextObject(User.class);
        userDbStorage.createUser(user);

        event = generator.nextObject(Event.class);
        event.setUserId(user.getId());
    }

    @Test
    void createEvent() {
        assertDoesNotThrow(
                () -> eventDbStorage.createEvent(event)
        );
    }

    @Test
    void getUserEvents() {
        Event createdEvent = eventDbStorage.createEvent(event);
        List<Event> userEvents = eventDbStorage.getUserEvents(user.getId());

        assertTrue(userEvents.stream().anyMatch(e -> e.getId() == createdEvent.getId()));
    }

    @Test
    void getEvent() {
        Event createdEvent = eventDbStorage.createEvent(event);

        assertDoesNotThrow(
                () -> eventDbStorage.getEvent(
                        createdEvent.getEntityId(), createdEvent.getEventType(), createdEvent.getOperation()
                )
        );
    }
}