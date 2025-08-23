package ru.gigachill.mapper.jooq;

import com.github.giga_chill.jooq.generated.enums.TaskStatus;
import com.github.giga_chill.jooq.generated.tables.records.TasksRecord;
import java.util.List;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.gigachill.dto.ShoppingListDTO;
import ru.gigachill.dto.TaskDTO;
import ru.gigachill.dto.TaskWithShoppingListsDTO;
import ru.gigachill.dto.UserDTO;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UsersRecordMapper.class})
public interface TasksRecordMapper {

    @Mapping(source = "status.literal", target = "status")
    // author/executor будут устанавливаться отдельно, т.к. это отдельные UsersRecord
    TaskDTO toTaskDTO(TasksRecord record);

    @Mapping(source = "dto.status", target = "status", qualifiedByName = "stringToTaskStatus")
    @Mapping(source = "dto.author.id", target = "authorId")
    @Mapping(source = "dto.executor.id", target = "executorId")
    TasksRecord toTasksRecord(TaskDTO dto, UUID eventId);

    @Named("stringToTaskStatus")
    default TaskStatus stringToTaskStatus(String status) {
        return status == null ? null : TaskStatus.valueOf(status);
    }

    TaskWithShoppingListsDTO toTaskWithShoppingListsDTO(
            TaskDTO dto, List<ShoppingListDTO> shoppingLists);

    /** Creates TaskWithShoppingListsDTO from TasksRecord with resolved author and executor */
    default TaskWithShoppingListsDTO toTaskWithShoppingListsDTO(
            TasksRecord record,
            List<ShoppingListDTO> shoppingLists,
            UserDTO author,
            UserDTO executor) {
        TaskDTO dto = toTaskDTO(record);
        dto.setAuthor(author);
        dto.setExecutor(executor);
        return toTaskWithShoppingListsDTO(dto, shoppingLists);
    }
}
