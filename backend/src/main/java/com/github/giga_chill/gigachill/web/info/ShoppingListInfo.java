package com.github.giga_chill.gigachill.web.info;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShoppingListInfo {
    @JsonProperty("shopping_list_id")
    private String shoppingListId;

    @JsonProperty("task_id")
    private String taskId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("status")
    private String status;

    @JsonProperty("can_edit")
    private Boolean canEdit;

    @JsonProperty("budget")
    private BigDecimal budget;

    @JsonProperty("shopping_items")
    private List<ShoppingItemInfo> shoppingItems;

    @JsonProperty("consumers")
    private List<ConsumerInfo> consumers;
}
