package com.github.giga_chill.gigachill.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Participant {
    private String id;
    private String login;
    private String name;
    private String role;
    private BigDecimal balance;
}
