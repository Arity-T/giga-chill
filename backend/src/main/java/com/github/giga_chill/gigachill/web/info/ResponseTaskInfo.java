package com.github.giga_chill.gigachill.web.info;

import java.util.List;

public record ResponseTaskInfo(
        String taskId,
        String title,
        String description,
        String status,
        String deadlineDatetime,
        String actualApprovalId,
        UserInfo author,
        UserInfo executor,
        List<ShoppingListInfo> shoppingLists
) {
}
