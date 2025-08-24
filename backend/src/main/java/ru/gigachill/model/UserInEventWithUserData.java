package ru.gigachill.model;

import com.github.giga_chill.jooq.generated.enums.EventRole;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class UserInEventWithUserData {
    private UUID userId;
    private UUID eventId;
    private EventRole role;
    private BigDecimal balance;
    private String login;
    private String name;
}
