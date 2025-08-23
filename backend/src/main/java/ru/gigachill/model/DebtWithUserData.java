package ru.gigachill.model;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;

@Data
public class DebtWithUserData {
    private UUID userId; // creditor_id или debtor_id в зависимости от контекста
    private BigDecimal amount;
    private String login;
    private String name;
}
