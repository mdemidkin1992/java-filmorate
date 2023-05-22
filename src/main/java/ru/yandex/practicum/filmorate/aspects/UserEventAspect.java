package ru.yandex.practicum.filmorate.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.aspects.annotation.SaveUserEvent;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.EventStorage;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.Instant;

@Aspect
@Component
@Slf4j
public class UserEventAspect {
    private final EventStorage eventStorage;

    public UserEventAspect(@Qualifier("eventDbStorage") EventStorage eventStorage) {
        this.eventStorage = eventStorage;
    }

    @Pointcut("@annotation(ru.yandex.practicum.filmorate.aspects.annotation.SaveUserEvent)")
    public void callMethodWithUserEvent() {
    }

    @After("callMethodWithUserEvent()")
    public void saveUserEvent(JoinPoint jp) throws NoSuchMethodException {
        MethodSignature signature = (MethodSignature) jp.getSignature();

        Method method = jp.getTarget().getClass().getMethod(
                signature.getMethod().getName(), signature.getMethod().getParameterTypes()
        );

        SaveUserEvent saveUserEvent = method.getAnnotation(SaveUserEvent.class);

        int indexOfUserIdArg = -1;
        int indexOfEntityIdArg = -1;

        String[] paramNames = signature.getParameterNames();
        for (int i = 0; i < paramNames.length; i++) {
            if (paramNames[i].equals(saveUserEvent.userIdParamName()))
                indexOfUserIdArg = i;
            else if (paramNames[i].equals(saveUserEvent.entityIdParamName()))
                indexOfEntityIdArg = i;
            else if (indexOfUserIdArg != -1 && indexOfEntityIdArg != -1)
                break;
        }
        if (indexOfUserIdArg == -1 || indexOfEntityIdArg == -1) {
            String errorMessage = "Could not find params to save user event";
            log.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        int userId = (int) jp.getArgs()[indexOfUserIdArg];
        int entityId = (int) jp.getArgs()[indexOfEntityIdArg];

        Event event = Event.builder()
                .userId(userId)
                .eventType(saveUserEvent.eventType())
                .operation(saveUserEvent.operation())
                .entityId(entityId)
                .timestamp(Timestamp.from(Instant.now()))
                .build();

        Event createdEvent = eventStorage.createEvent(event);
        log.info("UserEvent \"{}\" - \"{}\" with id \"{}\" saved successfully",
                event.getEventType(), event.getOperation(), createdEvent.getId());
    }

}
