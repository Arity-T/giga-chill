package com.github.giga_chill.gigachill.aspect;

import com.github.giga_chill.gigachill.config.LoggerColorConfig;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class RepositoryLoggerAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryLoggerAspect.class);
    private final LoggerColorConfig loggerColorConfig;

    @Pointcut("within(com.github.giga_chill.gigachill.repository..*)")
    public void repositoryMethods() {}

    @Around("repositoryMethods()")
    public Object logRepositoryCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;
            LOGGER.info(
                    loggerColorConfig.getREPO_COLOR()
                            + loggerColorConfig.getREPO_LABEL()
                            + "{}({}) -> {} ({} ms)"
                            + loggerColorConfig.getRESET_COLOR(),
                    methodName,
                    argsToString(args),
                    resultToString(result),
                    duration);
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - start;
            LOGGER.error(
                    loggerColorConfig.getREPO_COLOR()
                            + loggerColorConfig.getREPO_LABEL()
                            + "{}({}) threw {} ({} ms)"
                            + loggerColorConfig.getRESET_COLOR(),
                    methodName,
                    argsToString(args),
                    ex.getClass().getSimpleName(),
                    duration);
            throw ex;
        }
    }

    private String argsToString(Object[] args) {
        return args == null
                ? ""
                : java.util.Arrays.stream(args)
                        .map(String::valueOf)
                        .reduce((a, b) -> a + ", " + b)
                        .orElse("");
    }

    private String resultToString(Object result) {
        if (result instanceof Optional<?> opt) {
            return opt.map(Object::toString).orElse("Optional.empty");
        }
        return String.valueOf(result);
    }
}
