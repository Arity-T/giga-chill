package ru.gigachill.model;

import com.github.giga_chill.jooq.generated.enums.EventRole;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ConsumerWithUserData {
    private UUID userId;
    private String login;
    private String name;
    private EventRole role;
    private BigDecimal balance;
}

