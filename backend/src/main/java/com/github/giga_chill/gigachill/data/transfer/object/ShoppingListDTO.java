package com.github.giga_chill.gigachill.data.transfer.object;

import java.util.List;

public record ShoppingListDTO(String shoppingListId,
                              String taskId,
                              String title,
                              String description,
                              String status,
                              List<ShoppingItemDTO> shoppingItems,
                              List<ParticipantDTO> consumers) {
}
