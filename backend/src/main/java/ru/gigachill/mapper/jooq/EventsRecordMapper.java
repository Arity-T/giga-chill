package ru.gigachill.mapper.jooq;

import com.github.giga_chill.jooq.generated.tables.records.EventsRecord;
import org.mapstruct.Mapper;
import org.mapstruct.BeanMapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import ru.gigachill.data.transfer.object.EventDTO;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventsRecordMapper {

	EventDTO toEventDTO(EventsRecord record);
  
	EventsRecord toEventsRecord(EventDTO dto);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	void updateEventsRecordFromDTO(EventDTO dto, @MappingTarget EventsRecord record);
}
