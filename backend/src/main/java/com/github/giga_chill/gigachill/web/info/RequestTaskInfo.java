package com.github.giga_chill.gigachill.web.info;

import java.util.List;

public record RequestTaskInfo (String title,
                               String description,
                               String deadline_datetime,
                               String executor_id,
                               List<String> shopping_lists_ids){}