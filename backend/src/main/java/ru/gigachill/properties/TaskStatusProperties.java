package ru.gigachill.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "task-status")
public class TaskStatusProperties {
    private String open;
    private String inProgress;
    private String underReview;
    private String completed;
}
