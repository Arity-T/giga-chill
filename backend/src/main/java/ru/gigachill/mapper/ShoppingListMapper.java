package ru.gigachill.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gigachill.dto.ShoppingListDTO;
import ru.gigachill.web.api.model.ShoppingListWithItems;

@Mapper(
        componentModel = "spring",
        uses = {ParticipantMapper.class, ShoppingItemMapper.class})
public interface ShoppingListMapper {

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "canEdit", ignore = true)
    ShoppingListWithItems toShoppingListWithItems(ShoppingListDTO dto);
}
