package com.github.giga_chill.gigachill.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingItem {
    private UUID shoppingItemId;
    private String title;
    private BigDecimal quantity;
    private String unit;
    private Boolean isPurchased;

}
