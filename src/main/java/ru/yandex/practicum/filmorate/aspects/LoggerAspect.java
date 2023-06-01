package ru.yandex.practicum.filmorate.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String method = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        log.debug("{}.{}({})",
                className,
                method,
                joinArgs(args)
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

    private String joinArgs(Object[] array) {
        if (array == null || array.length == 0)
            return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Object obj = array[i];
            if (obj == null) {
                sb.append("null");
            } else if (obj instanceof Class) {
                sb.append(((Class<?>) obj).getSimpleName());
                sb.append(".class");
            } else if (obj instanceof String) {
                sb.append("\"".concat(obj.toString()).concat("\""));
            } else if (obj instanceof Object[]) {
                sb.append(Arrays.toString((Object[]) obj));
            } else {
                sb.append(obj);
            }
        }
        return sb.toString();
    }
}
