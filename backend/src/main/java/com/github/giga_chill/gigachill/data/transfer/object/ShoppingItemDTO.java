package com.github.giga_chill.gigachill.data.transfer.object;

import jakarta.annotation.Nullable;

import java.math.BigDecimal;

public record ShoppingItemDTO(String shoppingItemId,
                              @Nullable String title,
                              @Nullable BigDecimal quantity,
                              @Nullable String unit,
                              @Nullable Boolean isPurchased) {
}
