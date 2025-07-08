package com.github.giga_chill.gigachill.web.info;

public record ShoppingItem(String shopping_item_id,
                           String title,
                           String quantity,
                           String unit,
                           Boolean is_purchased) {
}
