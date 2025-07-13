package com.github.giga_chill.gigachill.web.info;

import java.math.BigDecimal;

public record ShoppingItemInfo(
        String shopping_item_id,
        String title,
        BigDecimal quantity,
        String unit,
        Boolean is_purchased) {}
