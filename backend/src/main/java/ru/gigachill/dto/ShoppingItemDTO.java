package ru.gigachill.dto;

import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingItemDTO {
    private UUID shoppingItemId;
    @Nullable private String title;
    @Nullable private BigDecimal quantity;
    @Nullable private String unit;
    @Nullable private Boolean isPurchased;
}
