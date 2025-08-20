package ru.gigachill.mapper.jooq;

import com.github.giga_chill.jooq.generated.tables.records.ShoppingItemsRecord;
import com.github.giga_chill.jooq.generated.tables.records.ShoppingListsRecord;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.gigachill.dto.ShoppingItemDTO;
import ru.gigachill.dto.ShoppingListDTO;
import ru.gigachill.dto.ParticipantDTO;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ParticipantsRecordMapper.class})
public interface ShoppingRecordsMapper {

	ShoppingItemDTO toShoppingItemDTO(ShoppingItemsRecord record);

	ShoppingItemsRecord toShoppingItemsRecord(ShoppingItemDTO dto, UUID shoppingListId);

	ShoppingListDTO toShoppingListDTOWithDetails(ShoppingListsRecord record, List<ShoppingItemDTO> shoppingItems, List<ParticipantDTO> consumers);
}
