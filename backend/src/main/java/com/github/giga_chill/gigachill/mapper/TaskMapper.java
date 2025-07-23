package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.TaskDTO;
import com.github.giga_chill.gigachill.data.transfer.object.TaskWithShoppingListsDTO;
import com.github.giga_chill.gigachill.web.info.ResponseTaskInfo;
import com.github.giga_chill.gigachill.web.info.ResponseTaskWithShoppingListsInfo;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    @Mapping(source = "taskId", target = "taskId", qualifiedByName = "uuidToString")
    ResponseTaskInfo toResponseTaskInfo(TaskDTO dto);

    @Mapping(source = "taskId", target = "taskId", qualifiedByName = "uuidToString")
    ResponseTaskWithShoppingListsInfo toResponseTaskWithShoppingListsInfo(
            TaskWithShoppingListsDTO dto);

    @Named("uuidToString")
    default String uuidToString(UUID eventId) {
        return eventId == null ? null : eventId.toString();
    }
}
