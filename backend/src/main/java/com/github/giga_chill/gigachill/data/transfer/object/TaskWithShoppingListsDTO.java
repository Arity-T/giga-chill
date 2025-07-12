package com.github.giga_chill.gigachill.data.transfer.object;

import jakarta.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public record TaskWithShoppingListsDTO(UUID taskId,
                                       String title,
                                       String description,
                                       String status,
                                       String deadlineDatetime,
                                       String actualApprovalId,
                                       UserDTO author,
                                       @Nullable UserDTO executor,
                                       List<ShoppingListDTO> shoppingLists) {
}
