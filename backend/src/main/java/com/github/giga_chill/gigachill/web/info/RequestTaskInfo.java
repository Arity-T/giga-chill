package com.github.giga_chill.gigachill.web.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import java.util.List;

public record RequestTaskInfo(
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("deadline_datetime") String deadlineDatetime,
        @Nullable @JsonProperty("executor_id") String executorId,
        @JsonProperty("shopping_lists_ids") List<String> shoppingListsIds) {}
