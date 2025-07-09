package com.github.giga_chill.gigachill.data.transfer.object;

import jakarta.annotation.Nullable;

public record ShoppingItemDTO(String shoppingItemId,
                              @Nullable String title,
                              @Nullable Integer quantity,
                              @Nullable String unit,
                              @Nullable Boolean isPurchased) {
}
