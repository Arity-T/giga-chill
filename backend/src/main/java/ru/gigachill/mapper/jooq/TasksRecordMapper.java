package ru.gigachill.mapper.jooq;

import com.github.giga_chill.jooq.generated.tables.records.TasksRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.gigachill.data.transfer.object.ShoppingListDTO;
import ru.gigachill.data.transfer.object.TaskDTO;
import com.github.giga_chill.jooq.generated.enums.TaskStatus;
import ru.gigachill.data.transfer.object.TaskWithShoppingListsDTO;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = {UsersRecordMapper.class})
public interface TasksRecordMapper {

	@Mapping(source = "status.literal", target = "status")
	// author/executor будут устанавливаться отдельно, т.к. это отдельные UsersRecord
	TaskDTO toTaskDTO(TasksRecord record);

	@Mapping(source = "status", target = "status", qualifiedByName = "stringToTaskStatus")
	@Mapping(source = "author.id", target = "authorId")
	@Mapping(source = "executor.id", target = "executorId")
	TasksRecord toTasksRecord(TaskDTO dto, UUID eventId);

	@Named("stringToTaskStatus")
	default TaskStatus stringToTaskStatus(String status) {
		return status == null ? null : TaskStatus.valueOf(status);
	}

	TaskWithShoppingListsDTO toTaskWithShoppingListsDTO(TaskDTO dto, List<ShoppingListDTO> shoppingLists);
}

