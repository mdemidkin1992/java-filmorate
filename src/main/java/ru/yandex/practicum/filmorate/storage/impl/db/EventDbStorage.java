package ru.yandex.practicum.filmorate.storage.impl.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EventNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.impl.db.mapper.EventMapper;
import ru.yandex.practicum.filmorate.utility.SqlQueries;

import java.sql.PreparedStatement;
import java.util.List;

@Component("eventDbStorage")
@Slf4j
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Event createEvent(Event event) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(SqlQueries.ADD_EVENT, new String[]{"EVENT_ID"});
            stmt.setInt(1, event.getUserId());
            stmt.setString(2, event.getEventType().name());
            stmt.setString(3, event.getOperation().name());
            stmt.setInt(4, event.getEntityId());
            stmt.setTimestamp(5, event.getTimestamp());
            return stmt;
        }, keyHolder);
        event.setId(keyHolder.getKey().intValue());
        return event;
    }

    @Override
    public List<Event> getUserEvents(int userId) {
        return jdbcTemplate
                .query(SqlQueries.GET_USER_EVENTS, new EventMapper(), userId);
    }

    @Override
    public Event getEvent(
            int entityId, EventType eventType, OperationType operationType
    ) throws EventNotFoundException {
        return jdbcTemplate.query(
                        SqlQueries.GET_USER_EVENT_BY_ENTITY_ID_AND_EVENT_TYPE,
                        new EventMapper(),
                        entityId, eventType.name(), operationType.name()
                ).stream()
                .findFirst()
                .orElseThrow(() ->
                        new EventNotFoundException(
                                "Event with entityId = " + entityId + " eventType = " + eventType
                                        + " operationType = " + operationType + " not found"
                        )
                );
    }
}
