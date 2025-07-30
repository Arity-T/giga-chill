package com.github.giga_chill.gigachill.data.transfer.object;

import jakarta.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskWithShoppingListsDTO {
    private UUID taskId;
    private String title;
    @Nullable private String description;
    private String status;
    private String deadlineDatetime;
    @Nullable private String executorComment;
    @Nullable private String reviewerComment;
    private UserDTO author;
    @Nullable private UserDTO executor;
    private List<ShoppingListDTO> shoppingLists;
}
