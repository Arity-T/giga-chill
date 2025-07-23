package com.github.giga_chill.gigachill.data.transfer.object;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantDTO{
    private UUID id;
    private String login;
    private String name;
    private String role;
    @Nullable private BigDecimal balance; }
