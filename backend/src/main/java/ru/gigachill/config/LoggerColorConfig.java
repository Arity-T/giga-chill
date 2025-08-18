package ru.gigachill.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@NoArgsConstructor
public class LoggerColorConfig {

    private final String POST_COLOR = "\u001b[32m";
    private final String GET_COLOR = "\u001b[36m";
    private final String DELETE_COLOR = "\u001b[31m";
    private final String PATCH_COLOR = "\u001b[35m";
    private final String PUT_COLOR = "\u001B[33m";
    private final String RESET_COLOR = "\u001B[0m";
    private final String EXCEPTION_COLOR = "\u001b[93m";
    private final String REPO_COLOR = "\u001b[94m"; // Светло-синий

    private final String GET_LABEL = "[GET]: ";
    private final String POST_LABEL = "[POST]: ";
    private final String PATCH_LABEL = "[PATCH]: ";
    private final String PUT_LABEL = "[PUT]: ";
    private final String DELETE_LABEL = "[DELETE]: ";
    private final String REPO_LABEL = "[REPO]: ";
    private final String EXCEPTION_LABEL = "[EXCEPTION]: ";

    private final String DB_COLOR = "\u001b[38;5;208m"; // Оранжевый
    private final String DB_LABEL = "[DB]: ";
    private final String DB_ERROR_COLOR = "\u001b[91m"; // Ярко-красный для ошибок БД
}
