package com.github.giga_chill.gigachill.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingList {
    private UUID shoppingListId;
    private UUID taskId;
    private String title;
    private String description;
    private String status;
    private BigDecimal budget;
    private List<ShoppingItem> shoppingItems;
    private List<Participant> consumers;
}
