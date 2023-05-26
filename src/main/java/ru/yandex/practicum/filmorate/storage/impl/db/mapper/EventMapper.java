package ru.yandex.practicum.filmorate.storage.impl.db.mapper;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;

import java.sql.ResultSet;
import java.sql.SQLException;


public class EventMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .id(rs.getInt("EVENT_ID"))
                .userId(rs.getInt("USER_ID"))
                .eventType(EventType.valueOf(rs.getString("EVENT_TYPE")))
                .operation(OperationType.valueOf(rs.getString("OPERATION")))
                .entityId(rs.getInt("ENTITY_ID"))
                .timestamp(rs.getTimestamp("EVENT_TIMESTAMP"))
                .build();
    }
}
