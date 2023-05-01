package ru.yandex.practicum.filmorate.storage.impl.db.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.IntNode;
import ru.yandex.practicum.filmorate.model.Film;

import java.io.IOException;

public class FilmDeserializer extends StdDeserializer<Film> {
    @Override
    public Film deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
//        JsonNode node = jp.getCodec().readTree(jp);
//        int filmId = (Integer) ((IntNode) node.get("id")).numberValue();
//        String filmName = node.get("name").asText();
//        int userId = (Integer) ((IntNode) node.get("createdBy")).numberValue();
//
//        return new Item(id, itemName, new User(userId, null));

        return null;
    }

    protected FilmDeserializer(Class<?> vc) {
        super(vc);
    }

    protected FilmDeserializer(JavaType valueType) {
        super(valueType);
    }

    protected FilmDeserializer(StdDeserializer<?> src) {
        super(src);
    }
}
