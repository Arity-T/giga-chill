package com.github.giga_chill.gigachill.data.transfer.object;

import java.util.List;
import java.util.UUID;

public record ShoppingListDTO(UUID shoppingListId,
                              String taskId,
                              String title,
                              String description,
                              String status,
                              List<ShoppingItemDTO> shoppingItems,
                              List<ParticipantDTO> consumers) {
}
