package ru.gigachill.mapper.jooq;

import com.github.giga_chill.jooq.generated.tables.records.EventsRecord;
import org.mapstruct.Mapper;
import ru.gigachill.data.transfer.object.EventDTO;

@Mapper(componentModel = "spring")
public interface EventsRecordMapper {

	EventDTO toEventDTO(EventsRecord record);
}
