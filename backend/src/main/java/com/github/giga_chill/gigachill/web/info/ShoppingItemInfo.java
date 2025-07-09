package com.github.giga_chill.gigachill.web.info;

public record ShoppingItemInfo(String shopping_item_id,
                               String title,
                               Integer quantity,
                               String unit,
                               Boolean is_purchased) {
}
