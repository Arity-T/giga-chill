package com.github.giga_chill.gigachill.model;


import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private UUID taskId;
    private String title;
    @Nullable private String description;
    private String status;
    private String deadlineDatetime;
    @Nullable private UUID actualApprovalId;
    private User author;
    @Nullable private User executor;
    private List<ShoppingList> shoppingLists;
}
