package ru.gigachill.mapper.jooq;

import com.github.giga_chill.jooq.generated.tables.records.ShoppingItemsRecord;
import com.github.giga_chill.jooq.generated.tables.records.ShoppingListsRecord;
import org.mapstruct.Mapper;
import ru.gigachill.data.transfer.object.ShoppingItemDTO;
import ru.gigachill.data.transfer.object.ShoppingListDTO;

@Mapper(componentModel = "spring")
public interface ShoppingRecordsMapper {

	ShoppingItemDTO toShoppingItemDTO(ShoppingItemsRecord record);

	ShoppingListDTO toShoppingListDTO(ShoppingListsRecord record);
}
