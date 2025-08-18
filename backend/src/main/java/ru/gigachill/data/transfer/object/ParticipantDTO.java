package ru.gigachill.data.transfer.object;

import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantDTO {
    private UUID id;
    private String login;
    private String name;
    private String role;
    @Nullable private BigDecimal balance;
}
