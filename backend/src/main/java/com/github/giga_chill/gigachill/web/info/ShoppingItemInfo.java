package com.github.giga_chill.gigachill.web.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingItemInfo{
        @JsonProperty("shopping_item_id") private String shoppingItemId;
        @JsonProperty("title") private String title;
        @JsonProperty("quantity") private BigDecimal quantity;
        @JsonProperty("unit") private String unit;
        @JsonProperty("is_purchased") private Boolean isPurchased;}
