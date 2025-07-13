package com.github.giga_chill.gigachill.web.info;

import java.util.List;

public record ShoppingListInfo(
        String shopping_list_id,
        String task_id,
        String title,
        String description,
        String status,
        Boolean can_edit,
        List<ShoppingItemInfo> shopping_items,
        List<ConsumerInfo> consumers) {}
