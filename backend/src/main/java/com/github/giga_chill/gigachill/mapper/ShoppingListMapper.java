package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.ShoppingListDTO;
import com.github.giga_chill.gigachill.web.info.ShoppingListInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {ParticipantMapper.class, ShoppingItemMapper.class, UuidMapper.class})
public interface ShoppingListMapper {

    @Mapping(source = "shoppingListId", target = "shoppingListId", qualifiedByName = "uuidToString")
    @Mapping(source = "taskId", target = "taskId", qualifiedByName = "uuidToString")
    ShoppingListInfo toShoppingListInfo(ShoppingListDTO dto);
}
