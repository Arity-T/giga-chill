package com.github.giga_chill.gigachill.web.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import java.util.Map;

public record ResponseTaskInfo(
        @JsonProperty("task_id") String taskId,
        @JsonProperty("title") String title,
        @JsonProperty("description") String description,
        @JsonProperty("status") String status,
        @JsonProperty("deadline_datetime") String deadlineDatetime,
        @JsonProperty("permissions") Map<String, Boolean> permissions,
        @JsonProperty("author") UserInfo author,
        @Nullable @JsonProperty("executor") UserInfo executor) {}
