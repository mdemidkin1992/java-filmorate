package ru.yandex.practicum.filmorate.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggerAspect {

    @Pointcut(
            "execution(* ru.yandex.practicum.filmorate..*.*(..)) " +
                    "&& !execution(* ru.yandex.practicum.filmorate.aspects..*(..))"
    )

    public void methodExecuting() {
    }

    @Around(value = "methodExecuting()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("{}.{}({})",
                joinPoint.getSourceLocation().getWithinType().getSimpleName(),
                joinPoint.getSignature().getName(),
                joinPoint.getArgs()
        );
        Object returnValue;
        try {
            returnValue = joinPoint.proceed();
        } catch (Exception e) {
            log.debug("{}.{}({}) throw exception class {} with message: {}",
                    joinPoint.getSourceLocation().getWithinType().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    joinPoint.getArgs(),
                    e.getClass().getSimpleName(),
                    e.getMessage()
            );
            throw e;
        }
        log.debug("{}.{} returned result: {} ",
                joinPoint.getSourceLocation().getWithinType().getSimpleName(),
                joinPoint.getSignature().getName(),
                returnValue
        );
        return returnValue;
    }

}
