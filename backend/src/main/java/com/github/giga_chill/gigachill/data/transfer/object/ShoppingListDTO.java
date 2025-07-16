package com.github.giga_chill.gigachill.data.transfer.object;

import jakarta.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ShoppingListDTO(
        UUID shoppingListId,
        @Nullable UUID taskId,
        String title,
        @Nullable String description,
        @Nullable BigDecimal budget,
        List<ShoppingItemDTO> shoppingItems,
        List<ParticipantDTO> consumers) {}
