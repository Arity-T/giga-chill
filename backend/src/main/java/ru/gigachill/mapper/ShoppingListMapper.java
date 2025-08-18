package ru.gigachill.mapper;

import org.mapstruct.Mapper;
import ru.gigachill.data.transfer.object.ShoppingListDTO;
import ru.gigachill.web.api.model.ShoppingListWithItems;

@Mapper(
        componentModel = "spring",
        uses = {ParticipantMapper.class, ShoppingItemMapper.class})
public interface ShoppingListMapper {

    ShoppingListWithItems toShoppingListWithItems(ShoppingListDTO dto);
}
