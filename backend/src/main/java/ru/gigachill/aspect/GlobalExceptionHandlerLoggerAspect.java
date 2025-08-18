package ru.gigachill.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.gigachill.config.LoggerColorConfig;

@Component
@Aspect
@RequiredArgsConstructor
public class GlobalExceptionHandlerLoggerAspect {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(GlobalExceptionHandlerLoggerAspect.class);
    private final LoggerColorConfig loggerColorConfig;

    @Pointcut("within(ru.gigachill.web.controller..*)")
    public void exceptionController() {}

    @Pointcut("within(ru.gigachill.service..*)")
    public void exceptionService() {}

    @Pointcut("within(ru.gigachill.data..*)")
    public void exceptionData() {}

    @Pointcut("within(ru.gigachill.repository..*)")
    public void exceptionRepository() {}

    @AfterThrowing(pointcut = "exceptionController()", throwing = "ex")
    public void logExceptionController(JoinPoint joinPoint, Throwable ex) {
        String method = joinPoint.getSignature().toShortString();
        LOGGER.error(
                loggerColorConfig.getEXCEPTION_COLOR()
                        + loggerColorConfig.getEXCEPTION_LABEL()
                        + "Method {} threw exception: {}"
                        + loggerColorConfig.getRESET_COLOR(),
                method,
                ex.toString());
    }

    @AfterThrowing(pointcut = "exceptionService()", throwing = "ex")
    public void logExceptionService(JoinPoint joinPoint, Throwable ex) {
        String method = joinPoint.getSignature().toShortString();
        LOGGER.error(
                loggerColorConfig.getEXCEPTION_COLOR()
                        + loggerColorConfig.getEXCEPTION_LABEL()
                        + "Method {} threw exception: {}"
                        + loggerColorConfig.getRESET_COLOR(),
                method,
                ex.toString());
    }

    @AfterThrowing(pointcut = "exceptionData()", throwing = "ex")
    public void logExceptionData(JoinPoint joinPoint, Throwable ex) {
        String method = joinPoint.getSignature().toShortString();
        LOGGER.error(
                loggerColorConfig.getEXCEPTION_COLOR()
                        + loggerColorConfig.getEXCEPTION_LABEL()
                        + "Method {} threw exception: {}"
                        + loggerColorConfig.getRESET_COLOR(),
                method,
                ex.toString());
    }

    @AfterThrowing(pointcut = "exceptionRepository()", throwing = "ex")
    public void logExceptionRepository(JoinPoint joinPoint, Throwable ex) {
        String method = joinPoint.getSignature().toShortString();
        LOGGER.error(
                loggerColorConfig.getEXCEPTION_COLOR()
                        + loggerColorConfig.getEXCEPTION_LABEL()
                        + "Method {} threw exception: {}"
                        + loggerColorConfig.getRESET_COLOR(),
                method,
                ex.toString());
    }
}
