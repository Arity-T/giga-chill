package com.github.giga_chill.gigachill.data.transfer.object;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingItemDTO{
        private UUID shoppingItemId;
        @Nullable private String title;
        @Nullable private BigDecimal quantity;
        @Nullable private String unit;
        @Nullable private Boolean isPurchased;}
