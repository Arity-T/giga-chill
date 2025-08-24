package ru.gigachill.model;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Data;
import org.springframework.lang.Nullable;

@Data
public class ShoppingListWithDetails {
    private UUID shoppingListId;
    private UUID taskId;
    private UUID eventId;
    private String title;
    private String description;
    private String fileLink;
    private BigDecimal budget;

    // Shopping item fields
    @Nullable private UUID shoppingItemId;
    @Nullable private String itemTitle;
    @Nullable private BigDecimal quantity;
    @Nullable private String unit;
    @Nullable private Boolean isPurchased;

    // Consumer field
    @Nullable private UUID userId;
}
