package ru.gigachill.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "roles")
public class RoleProperties {
    private String owner;
    private String admin;
    private String participant;
}
