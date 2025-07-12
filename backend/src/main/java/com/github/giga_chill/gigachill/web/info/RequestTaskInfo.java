package com.github.giga_chill.gigachill.web.info;

import jakarta.annotation.Nullable;

import java.util.List;

public record RequestTaskInfo (String title,
                               String description,
                               String deadline_datetime,
                               @Nullable String executor_id,
                               List<String> shopping_lists_ids){}