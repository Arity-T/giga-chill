package com.github.giga_chill.gigachill.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class GlobalExceptionHandlerLoggerAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandlerLoggerAspect.class);
    private static final String EXCEPTION_COLOR = "\u001b[33m";
    private static final String RESET_COLOR = "\u001B[0m";

    @Pointcut("within(com.github.giga_chill.gigachill.web.controller..*)")
    public void exceptionController() {
    }

    @Pointcut("within(com.github.giga_chill.gigachill.service..*)")
    public void exceptionService() {
    }

    @AfterThrowing(pointcut = "exceptionController()", throwing = "ex")
    public void logExceptionController(JoinPoint joinPoint, Throwable ex) {
        String method = joinPoint.getSignature().toShortString();
        LOGGER.error(EXCEPTION_COLOR + "Method {} threw exception: {}" + RESET_COLOR, method, ex.toString());
    }

    @AfterThrowing(pointcut = "exceptionService()", throwing = "ex")
    public void logExceptionService(JoinPoint joinPoint, Throwable ex) {
        String method = joinPoint.getSignature().toShortString();
        LOGGER.error(EXCEPTION_COLOR + "Method {} threw exception: {}" + RESET_COLOR, method, ex.toString());
    }

}
