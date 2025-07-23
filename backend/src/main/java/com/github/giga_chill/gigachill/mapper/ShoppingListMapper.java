package com.github.giga_chill.gigachill.mapper;


import com.github.giga_chill.gigachill.data.transfer.object.ParticipantDTO;
import com.github.giga_chill.gigachill.data.transfer.object.ShoppingListDTO;
import com.github.giga_chill.gigachill.web.info.ParticipantInfo;
import com.github.giga_chill.gigachill.web.info.ShoppingListInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ShoppingListMapper {

    @Mapping(source = "shoppingListId", target = "shoppingListId", qualifiedByName = "uuidToString")
    @Mapping(source = "taskId", target = "taskId", qualifiedByName = "uuidToString")
    ShoppingListInfo toShoppingListInfo (ShoppingListDTO dto);



    @Named("uuidToString")
    default String uuidToString(UUID eventId) {
        return eventId == null ? null : eventId.toString();
    }
}
