package com.github.giga_chill.gigachill.model;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Participant {
    private UUID id;
    private String login;
    private String name;
    private String role;
    private BigDecimal balance;
}
