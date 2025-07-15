package com.github.giga_chill.gigachill.web.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import java.util.List;

public record ResponseTaskWithShoppingListsInfo(
        @JsonProperty("task_id") String taskId,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("status") String status,
        @JsonProperty("deadline_datetime") String deadlineDatetime,
        @JsonProperty("actual_approval_id") String actualApprovalId,
        @JsonProperty("author") UserInfo author,
        @Nullable @JsonProperty("executor")  UserInfo executor,
        @JsonProperty("shopping_lists") List<ShoppingListInfo> shoppingLists) {}
