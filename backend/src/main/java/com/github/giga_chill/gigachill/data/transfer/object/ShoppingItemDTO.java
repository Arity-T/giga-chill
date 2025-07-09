package com.github.giga_chill.gigachill.data.transfer.object;

public record ShoppingItemDTO(String shoppingItemId,
                              String title,
                              Integer quantity,
                              String unit,
                              Boolean isPurchased) {
}
