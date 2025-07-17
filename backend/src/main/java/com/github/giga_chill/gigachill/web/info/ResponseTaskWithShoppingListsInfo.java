package com.github.giga_chill.gigachill.web.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Map;

public record ResponseTaskWithShoppingListsInfo(
        @JsonProperty("task_id") String taskId,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("status") String status,
        @JsonProperty("deadline_datetime") String deadlineDatetime,
        @Nullable @JsonProperty("executor_comment") String executorComment,
        @Nullable @JsonProperty("reviewer_comment") String reviewerComment,
        @JsonProperty("permissions") Map<String, Boolean> permissions,
        @JsonProperty("author") UserInfo author,
        @Nullable @JsonProperty("executor") UserInfo executor,
        @JsonProperty("shopping_lists") List<ShoppingListInfo> shoppingLists) {}
