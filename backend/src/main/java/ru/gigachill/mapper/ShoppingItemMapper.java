package ru.gigachill.mapper;

import org.mapstruct.Mapper;
import ru.gigachill.dto.ShoppingItemDTO;
import ru.gigachill.web.api.model.ShoppingItem;

@Mapper(componentModel = "spring")
public interface ShoppingItemMapper {

    ShoppingItem toShoppingItem(ShoppingItemDTO dto);
}
