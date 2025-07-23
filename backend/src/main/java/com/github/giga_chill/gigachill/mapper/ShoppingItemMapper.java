package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.ShoppingItemDTO;
import com.github.giga_chill.gigachill.web.info.ShoppingItemInfo;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShoppingItemMapper {

    @Mapping(source = "shoppingItemId", target = "shoppingItemId", qualifiedByName = "uuidToString")
    ShoppingItemInfo toShoppingItemInfo(ShoppingItemDTO dto);

    @Named("uuidToString")
    default String uuidToString(UUID eventId) {
        return eventId == null ? null : eventId.toString();
    }
}
