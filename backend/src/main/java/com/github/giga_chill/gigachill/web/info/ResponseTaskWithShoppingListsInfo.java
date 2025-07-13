package com.github.giga_chill.gigachill.web.info;

import jakarta.annotation.Nullable;
import java.util.List;

public record ResponseTaskWithShoppingListsInfo(
        String taskId,
        String title,
        String description,
        String status,
        String deadlineDatetime,
        String actualApprovalId,
        UserInfo author,
        @Nullable UserInfo executor,
        List<ShoppingListInfo> shoppingLists) {}
