package com.github.giga_chill.gigachill.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingList {
    private String shoppingListId;
    private String taskId;
    private String title;
    private String description;
    private String status;
    private List<ShoppingItem> shoppingItems;
    private List<Participant> consumers;
}
