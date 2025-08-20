package ru.gigachill.mapper.jooq;

import com.github.giga_chill.jooq.generated.tables.records.TasksRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gigachill.data.transfer.object.TaskDTO;

@Mapper(componentModel = "spring", uses = {UsersRecordMapper.class})
public interface TasksRecordMapper {

	@Mapping(source = "status.literal", target = "status")
	// author/executor будут устанавливаться отдельно, т.к. это отдельные UsersRecord
	TaskDTO toTaskDTO(TasksRecord record);
}

