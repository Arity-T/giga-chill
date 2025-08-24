package ru.gigachill.mapper.jooq;

import com.github.giga_chill.jooq.generated.tables.records.EventsRecord;
import org.mapstruct.*;
import ru.gigachill.dto.EventDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventsRecordMapper {

    EventDTO toEventDTO(EventsRecord record);

    // Менять поле isFinalized можно только отдельным методом
    @Mapping(target = "isFinalized", ignore = true)
    EventsRecord toEventsRecord(EventDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEventsRecordFromDTO(EventDTO dto, @MappingTarget EventsRecord record);
}
