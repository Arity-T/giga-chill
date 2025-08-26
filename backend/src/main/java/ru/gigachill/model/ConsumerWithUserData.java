package ru.gigachill.model;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;
import ru.gigachill.jooq.generated.enums.EventRole;

@Data
public class ConsumerWithUserData {
    private UUID shoppingListId;
    private UUID userId;
    private String login;
    private String name;
    private EventRole role;
    private BigDecimal balance;
}
