package ru.yandex.practicum.filmorate.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.aspects.annotation.SaveUserEvent;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.OperationType;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class UserEventAspect {
    private final EventStorage eventStorage;
    private final ReviewStorage reviewStorage;

    public UserEventAspect(
            @Qualifier("eventDbStorage")
            EventStorage eventStorage,
            @Qualifier("reviewDbStorage")
            ReviewStorage reviewStorage
    ) {
        this.eventStorage = eventStorage;
        this.reviewStorage = reviewStorage;
    }

    @Pointcut("@annotation(ru.yandex.practicum.filmorate.aspects.annotation.SaveUserEvent)")
    public void callMethodWithUserEvent() {
    }

    @AfterReturning("callMethodWithUserEvent()")
    public void saveUserEvent(JoinPoint jp) {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();

        SaveUserEvent saveUserEvent = method.getAnnotation(SaveUserEvent.class);

        final Identifiers identifiers;

        if (saveUserEvent.entityClass() == SaveUserEvent.None.class
                && !saveUserEvent.userIdParamName().equals(SaveUserEvent.None.NONE_PARAM))
            identifiers = getIdentifiersFromArgs(signature.getParameterNames(), jp.getArgs(), saveUserEvent);
        else if (saveUserEvent.userIdParamName().equals(SaveUserEvent.None.NONE_PARAM)
                && saveUserEvent.operation() == OperationType.REMOVE)
            identifiers = getIdentifiersByEntityIdOfRemoveOperation(
                    signature.getParameterNames(), jp.getArgs(), saveUserEvent
            );
        else
            identifiers = getIdentifiersFromObject(saveUserEvent.entityClass(), jp.getArgs());


        Event event = Event.builder()
                .userId(identifiers.getUserId())
                .eventType(saveUserEvent.eventType())
                .operation(saveUserEvent.operation())
                .entityId(identifiers.getEntityId())
                .timestamp(Timestamp.from(Instant.now()))
                .build();

        Event createdEvent = eventStorage.createEvent(event);
        log.info("UserEvent \"{}\" - \"{}\" (id = {}; userId = {}) saved successfully",
                event.getEventType(), event.getOperation(), createdEvent.getId(), createdEvent.getUserId());
    }

    private Identifiers getIdentifiersFromArgs(String[] paramNames, Object[] args, SaveUserEvent saveUserEvent) {
        int indexOfUserIdArg = -1;
        int indexOfEntityIdArg = -1;

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
            throw new IllegalArgumentException(errorMessage);
        }

        return new Identifiers((int) args[indexOfUserIdArg], (int) args[indexOfEntityIdArg]);
    }

    private Identifiers getIdentifiersByEntityIdOfRemoveOperation(
            String[] paramNames, Object[] args, SaveUserEvent saveUserEvent
    ) {
        int indexOfEntityIdArg = -1;
        for (int i = 0; i < paramNames.length; i++)
            if (paramNames[i].equals(saveUserEvent.entityIdParamName())) {
                indexOfEntityIdArg = i;
                break;
            }

        if (indexOfEntityIdArg == -1) {
            String errorMessage = "Could not find entityIdParam in params";
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        int entityId = (int) args[indexOfEntityIdArg];
        Event event = eventStorage.getEvent(entityId, saveUserEvent.eventType(), OperationType.ADD);

        return new Identifiers(event.getUserId(), event.getEntityId());
    }

    private Identifiers getIdentifiersFromObject(Class<?> entityClass, Object[] args) {
        if (entityClass == Review.class) {
            Review inputReview = (Review) Arrays.stream(args)
                    .filter(arg -> arg.getClass() == Review.class)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Could not find param with Review type"));

            Review review = reviewStorage.getReviewById(inputReview.getReviewId());
            return new Identifiers(review.getUserId(), review.getReviewId());
        } else
            throw new IllegalArgumentException(
                    "No action found to handle parameter with type " + entityClass
            );
    }

    private static class Identifiers {
        private final int userId;
        private final int entityId;

        public Identifiers(int userId, int entityId) {
            this.userId = userId;
            this.entityId = entityId;
        }

        public int getUserId() {
            return userId;
        }

        public int getEntityId() {
            return entityId;
        }
    }
}
