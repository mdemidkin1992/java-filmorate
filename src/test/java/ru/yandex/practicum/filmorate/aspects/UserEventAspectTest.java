package ru.yandex.practicum.filmorate.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.lang.reflect.Method;

@ExtendWith(MockitoExtension.class)
class UserEventAspectTest {

    private final EasyRandom generator = new EasyRandom();
    private final EventStorage mockEventStorage = Mockito.mock(EventStorage.class);
    private final ReviewStorage mockReviewStorage = Mockito.mock(ReviewStorage.class);

    private final UserEventAspect userEventAspect = new UserEventAspect(
            mockEventStorage, mockReviewStorage
    );

    private final JoinPoint mockJoinPoint = Mockito.mock(JoinPoint.class);


    @BeforeEach
    public void beforeEach() {
        Mockito.when(mockEventStorage.createEvent(Mockito.any(Event.class))).thenAnswer(
                (Answer<Event>) invocationOnMock -> invocationOnMock.getArgument(0)
        );

        Mockito.when(mockReviewStorage.getReviewById(Mockito.anyInt())).thenReturn(
                generator.nextObject(Review.class)
        );
    }

    @Test
    void saveUserEventWhenGetIdentifiersFromArgs() throws NoSuchMethodException {
        MethodSignature mockMethodSignature = Mockito.mock(MethodSignature.class);
        Method method = UserService.class.getMethod("addFriend", int.class, int.class);

        Mockito.when(mockJoinPoint.getSignature()).thenReturn(mockMethodSignature);
        Mockito.when(mockMethodSignature.getMethod()).thenReturn(method);
        Mockito.when(mockMethodSignature.getParameterNames()).thenReturn(new String[]{"userId", "friendId"});
        Mockito.when(mockJoinPoint.getArgs()).thenReturn(new Object[]{1, 2});


        userEventAspect.saveUserEvent(mockJoinPoint);
        Mockito.verify(mockEventStorage, Mockito.times(1)).createEvent(Mockito.any(Event.class));
    }

    @Test
    void saveUserEventWhenGetIdentifiersFromObject() throws NoSuchMethodException {
        MethodSignature mockMethodSignature = Mockito.mock(MethodSignature.class);
        Method method = ReviewService.class.getMethod("updateReview", Review.class);

        Mockito.when(mockJoinPoint.getSignature()).thenReturn(mockMethodSignature);
        Mockito.when(mockMethodSignature.getMethod()).thenReturn(method);
        Mockito.when(mockJoinPoint.getArgs()).thenReturn(new Object[]{generator.nextObject(Review.class)});

        userEventAspect.saveUserEvent(mockJoinPoint);
        Mockito.verify(mockEventStorage, Mockito.times(1)).createEvent(Mockito.any(Event.class));
    }

    @Test
    void saveUserEventWhenGetIdentifiersByEntityIdOfRemoveOperation() throws NoSuchMethodException {
        MethodSignature mockMethodSignature = Mockito.mock(MethodSignature.class);
        Method method = ReviewService.class.getMethod("deleteReviewById", int.class);

        Mockito.when(mockJoinPoint.getSignature()).thenReturn(mockMethodSignature);
        Mockito.when(mockMethodSignature.getMethod()).thenReturn(method);
        Mockito.when(mockMethodSignature.getParameterNames()).thenReturn(new String[]{"reviewId"});
        Mockito.when(mockJoinPoint.getArgs()).thenReturn(new Object[]{1});
        Mockito.when(mockEventStorage.getEvent(
                Mockito.anyInt(), Mockito.eq(EventType.REVIEW), Mockito.eq(OperationType.ADD))
        ).thenReturn(generator.nextObject(Event.class));

        userEventAspect.saveUserEvent(mockJoinPoint);
        Mockito.verify(mockEventStorage, Mockito.times(1)).createEvent(Mockito.any(Event.class));
    }
}