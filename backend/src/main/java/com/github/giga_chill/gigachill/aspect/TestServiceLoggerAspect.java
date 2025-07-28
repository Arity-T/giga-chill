package com.github.giga_chill.gigachill.aspect;

import com.github.giga_chill.gigachill.config.LoggerColorConfig;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
@Profile("test")
public class TestServiceLoggerAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataAccessLoggerAspect.class);
    private final LoggerColorConfig loggerColorConfig;

    // Покрываем оба пакета: repository и impl
    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.TestService.cleanBD(..))")
    public void cleanBD() {}

    @Around("cleanBD()")
    public Object logCleanBD(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getDB_COLOR()
                            + loggerColorConfig.getDB_LABEL()
                            + "Data base was cleaned"
                            + loggerColorConfig.getRESET_COLOR());
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }
}
