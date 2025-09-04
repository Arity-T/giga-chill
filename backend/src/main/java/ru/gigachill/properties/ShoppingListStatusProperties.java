package ru.gigachill.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "shopping-list-status")
public class ShoppingListStatusProperties {
    private String unassigned;
    private String assigned;
    private String inProgress;
    private String bought;
    private String partiallyBought;
    private String cancelled;
}
