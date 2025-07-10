package com.github.giga_chill.gigachill.data.transfer.object;

import jakarta.annotation.Nullable;

import java.math.BigDecimal;
import java.util.UUID;

public record ShoppingItemDTO(UUID shoppingItemId,
                              @Nullable String title,
                              @Nullable BigDecimal quantity,
                              @Nullable String unit,
                              @Nullable Boolean isPurchased) {
}
