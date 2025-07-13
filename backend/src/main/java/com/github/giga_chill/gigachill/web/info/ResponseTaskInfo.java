package com.github.giga_chill.gigachill.web.info;

import jakarta.annotation.Nullable;

public record ResponseTaskInfo(
        String taskId,
        String title,
        String description,
        String status,
        String deadlineDatetime,
        String actualApprovalId,
        UserInfo author,
        @Nullable UserInfo executor) {}
