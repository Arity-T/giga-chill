package com.github.giga_chill.gigachill.aspect;


import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Aspect;
import java.util.Optional;

@Component
@Aspect
public class RepositoryLoggerAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryLoggerAspect.class);

    private static final String REPO_COLOR = "\u001b[94m";  // Светло-синий
    private static final String RESET_COLOR = "\u001B[0m";

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
            LOGGER.info(REPO_COLOR + "[REPO] {}({}) -> {} ({} ms)" + RESET_COLOR,
                    methodName,
                    argsToString(args),
                    resultToString(result),
                    duration
            );
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - start;
            LOGGER.error(REPO_COLOR + "[REPO] {}({}) threw {} ({} ms)" + RESET_COLOR,
                    methodName,
                    argsToString(args),
                    ex.getClass().getSimpleName(),
                    duration
            );
            throw ex;
        }
    }

    private String argsToString(Object[] args) {
        return args == null ? "" : java.util.Arrays.stream(args)
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

