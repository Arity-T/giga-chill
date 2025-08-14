package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.ShoppingItemDTO;
import com.github.giga_chill.gigachill.web.api.model.ShoppingItem;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShoppingItemMapper {

    ShoppingItem toShoppingItem(ShoppingItemDTO dto);
}
