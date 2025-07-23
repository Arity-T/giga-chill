package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.ShoppingListDTO;
import com.github.giga_chill.gigachill.web.info.ShoppingListInfo;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShoppingListMapper {

    @Mapping(source = "shoppingListId", target = "shoppingListId", qualifiedByName = "uuidToString")
    @Mapping(source = "taskId", target = "taskId", qualifiedByName = "uuidToString")
    ShoppingListInfo toShoppingListInfo(ShoppingListDTO dto);

    @Named("uuidToString")
    default String uuidToString(UUID eventId) {
        return eventId == null ? null : eventId.toString();
    }
}
