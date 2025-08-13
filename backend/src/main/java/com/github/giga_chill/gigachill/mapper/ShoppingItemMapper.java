package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.ShoppingItemDTO;
import com.github.giga_chill.gigachill.web.api.model.ShoppingItem;
import com.github.giga_chill.gigachill.web.info.ShoppingItemInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UuidMapper.class)
public interface ShoppingItemMapper {

    @Mapping(source = "shoppingItemId", target = "shoppingItemId", qualifiedByName = "uuidToString")
    ShoppingItemInfo toShoppingItemInfo(ShoppingItemDTO dto);

    ShoppingItem toShoppingItem(ShoppingItemDTO dto);
}
