package ru.gigachill.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.gigachill.dto.ShoppingItemDTO;
import ru.gigachill.dto.ShoppingListDTO;
import ru.gigachill.model.ShoppingListWithDetails;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShoppingListWithDetailsMapper {

    @Mapping(target = "shoppingItems", ignore = true)
    @Mapping(target = "consumers", ignore = true)
    ShoppingListDTO toShoppingListDTO(ShoppingListWithDetails details);

    @Mapping(source = "itemTitle", target = "title")
    ShoppingItemDTO toShoppingItemDTO(ShoppingListWithDetails details);
}