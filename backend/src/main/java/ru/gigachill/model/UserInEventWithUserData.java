package ru.gigachill.model;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;
import ru.gigachill.jooq.generated.enums.EventRole;

@Data
public class UserInEventWithUserData {
    private UUID userId;
    private UUID eventId;
    private EventRole role;
    private BigDecimal balance;
    private String login;
    private String name;
}
