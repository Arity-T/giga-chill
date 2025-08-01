package com.github.giga_chill.gigachill.web.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseTaskInfo {
    @JsonProperty("task_id")
    private String taskId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private String status;

    @JsonProperty("deadline_datetime")
    private String deadlineDatetime;

    @Nullable
    @JsonProperty("executor_comment")
    private String executorComment;

    @Nullable
    @JsonProperty("reviewer_comment")
    private String reviewerComment;

    @JsonProperty("permissions")
    private Map<String, Boolean> permissions;

    @JsonProperty("author")
    private UserInfo author;

    @Nullable
    @JsonProperty("executor")
    private UserInfo executor;
}
