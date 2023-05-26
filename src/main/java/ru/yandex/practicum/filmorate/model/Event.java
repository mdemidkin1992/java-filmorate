package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;

import java.sql.Timestamp;

@Data
@Builder
public class Event {
    @JsonProperty("eventId")
    private int id;
    private int userId;
    private EventType eventType;
    private OperationType operation;
    private int entityId;
    @JsonIgnore
    private Timestamp timestamp;

    @JsonProperty("timestamp")
    public long getTimestampMillisecond() {
        return timestamp.getTime();
    }
}
