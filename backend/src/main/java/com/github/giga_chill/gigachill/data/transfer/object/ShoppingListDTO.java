package com.github.giga_chill.gigachill.data.transfer.object;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingListDTO{
        private UUID shoppingListId;
        @Nullable private UUID taskId;
    private String title;
        @Nullable private String description;
        @Nullable private BigDecimal budget;
    private List<ShoppingItemDTO> shoppingItems;
    private List<ParticipantDTO> consumers;}
