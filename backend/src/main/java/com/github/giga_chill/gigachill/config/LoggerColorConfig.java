package com.github.giga_chill.gigachill.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@NoArgsConstructor
public class LoggerColorConfig {

    private static final String POST_COLOR = "\u001b[32m";
    private static final String GET_COLOR = "\u001b[36m";
    private static final String DELETE_COLOR = "\u001b[31m";
    private static final String PATCH_COLOR = "\u001b[35m";
    private static final String RESET_COLOR = "\u001B[0m";
    private static final String EXCEPTION_COLOR = "\u001b[33m";
    private static final String REPO_COLOR = "\u001b[94m";  // Светло-синий

}
