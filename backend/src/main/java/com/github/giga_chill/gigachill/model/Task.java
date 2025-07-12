package com.github.giga_chill.gigachill.model;


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
    private String description;
    private String status;
    private String deadlineDatetime;
    private UUID actualApprovalId;
    private User author;
    private User executor;
    private List<ShoppingList> shoppingLists;
}
