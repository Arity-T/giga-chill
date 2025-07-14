package com.github.giga_chill.gigachill.web.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record ShoppingListInfo(
        @JsonProperty("shopping_list_id") String shoppingListId,
        @JsonProperty("task_id") String taskId,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("status") String status,
        @JsonProperty("can_edit") Boolean canEdit,
        @JsonProperty("shopping_items") List<ShoppingItemInfo> shoppingItems,
        @JsonProperty("consumers") List<ConsumerInfo> consumers) {}
