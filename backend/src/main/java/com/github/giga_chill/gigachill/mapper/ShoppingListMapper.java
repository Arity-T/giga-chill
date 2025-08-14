package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.ShoppingListDTO;
import com.github.giga_chill.gigachill.web.api.model.ShoppingListWithItems;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {ParticipantMapper.class, ShoppingItemMapper.class})
public interface ShoppingListMapper {

    ShoppingListWithItems toShoppingListWithItems(ShoppingListDTO dto);
}
