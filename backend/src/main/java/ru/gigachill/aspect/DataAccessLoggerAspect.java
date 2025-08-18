package ru.gigachill.aspect;

import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.gigachill.config.LoggerColorConfig;

@Component
@Aspect
@RequiredArgsConstructor
public class DataAccessLoggerAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataAccessLoggerAspect.class);
    private final LoggerColorConfig loggerColorConfig;

    // Покрываем оба пакета: repository и impl
    @Pointcut(
            "within(ru.gigachill.repository..*) || within(ru.gigachill.data.access.object.impl..*)")
    public void dataAccessMethods() {}

    @Around("dataAccessMethods()")
    public Object logDataAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        String userInfo = getCurrentUserInfo();

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - start;

            LOGGER.info(
                    loggerColorConfig.getDB_COLOR()
                            + loggerColorConfig.getDB_LABEL()
                            + "[{}] {}.{}({}) -> {} ({} ms)"
                            + loggerColorConfig.getRESET_COLOR(),
                    userInfo,
                    className,
                    methodName,
                    formatArgs(joinPoint),
                    summarizeResult(result),
                    duration);
            return result;
        } catch (Throwable ex) {
            long duration = System.currentTimeMillis() - start;
            LOGGER.error(
                    loggerColorConfig.getDB_ERROR_COLOR()
                            + loggerColorConfig.getDB_LABEL()
                            + "[{}] {}.{}({}) threw {}: {} ({} ms)"
                            + loggerColorConfig.getRESET_COLOR(),
                    userInfo,
                    className,
                    methodName,
                    formatArgs(joinPoint),
                    ex.getClass().getSimpleName(),
                    ex.getMessage(),
                    duration,
                    ex);
            throw ex;
        }
    }

    private String getCurrentUserInfo() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null
                    && authentication.isAuthenticated()
                    && authentication.getPrincipal() != null) {
                Object principal = authentication.getPrincipal();
                if (principal
                        instanceof
                        org.springframework.security.core.userdetails.UserDetails
                        userDetails) {
                    return userDetails.getUsername();
                } else {
                    return principal.toString();
                }
            }
        } catch (Exception ignored) {
        }
        return "anonymous";
    }

    private String formatArgs(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String[] paramNames =
                Arrays.stream(joinPoint.getSignature().getDeclaringType().getDeclaredMethods())
                        .filter(m -> m.getName().equals(joinPoint.getSignature().getName()))
                        .findFirst()
                        .map(
                                m ->
                                        Arrays.stream(m.getParameters())
                                                .map(Parameter::getName)
                                                .toArray(String[]::new))
                        .orElse(new String[args.length]);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            String name = (paramNames.length > i) ? paramNames[i] : "arg" + i;
            sb.append(name).append("=").append(summarizeObject(args[i]));
            if (i < args.length - 1) sb.append(", ");
        }
        return sb.toString();
    }

    private String summarizeResult(Object result) {
        return summarizeObject(result);
    }

    private String summarizeObject(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof Optional<?> opt) {
            return opt.map(this::summarizeObject).orElse("Optional.empty");
        }
        if (obj instanceof Collection<?> col) {
            StringBuilder sb = new StringBuilder("Collection(size=" + col.size());
            if (!col.isEmpty()) {
                sb.append(", first=[");
                int count = 0;
                for (Object item : col) {
                    sb.append(summarizeObject(item));
                    if (++count >= 3) break;
                    sb.append(", ");
                }
                sb.append("]");
            }
            sb.append(")");
            return sb.toString();
        }
        if (obj instanceof Map<?, ?> map) {
            return "Map(size=" + map.size() + ")";
        }
        // Jooq Result/Record
        if (obj instanceof org.jooq.Result<?> res) {
            return "JooqResult(size=" + res.size() + ")";
        }
        if (obj instanceof org.jooq.Record rec) {
            StringBuilder sb = new StringBuilder("JooqRecord{");
            for (int i = 0; i < rec.size(); i++) {
                sb.append(rec.field(i).getName()).append("=").append(rec.get(i));
                if (i < rec.size() - 1) sb.append(", ");
            }
            sb.append("}");
            return sb.toString();
        }
        // DTO/Entity: логируем только ключевые поля
        try {
            var clazz = obj.getClass();
            var fields =
                    Arrays.stream(clazz.getDeclaredFields())
                            .filter(
                                    f ->
                                            List.of("id", "eventId", "userId", "name")
                                                    .contains(f.getName()))
                            .peek(f -> f.setAccessible(true))
                            .map(
                                    f -> {
                                        try {
                                            return f.getName() + "=" + f.get(obj);
                                        } catch (Exception e) {
                                            return "";
                                        }
                                    })
                            .collect(Collectors.joining(", "));
            if (!fields.isEmpty()) {
                return clazz.getSimpleName() + "{" + fields + "}";
            }
        } catch (Exception ignored) {
        }
        // Fallback: toString, но не больше 200 символов
        String str = obj.toString();
        return str.length() > 200 ? str.substring(0, 200) + "..." : str;
    }
}
