package ru.yandex.practicum.filmorate.aspects.annotation;

import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SaveUserEvent {
    EventType eventType();

    OperationType operation();

    String userIdParamName() default "userId";

    String entityIdParamName();
}
