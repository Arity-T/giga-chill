package com.github.giga_chill.gigachill.web.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public record ShoppingItemInfo(
        @JsonProperty("shopping_item_id") String shoppingItemId,
        @JsonProperty("title") String title,
        @JsonProperty("quantity") BigDecimal quantity,
        @JsonProperty("unit") String unit,
        @JsonProperty("is_purchased") Boolean isPurchased) {}
