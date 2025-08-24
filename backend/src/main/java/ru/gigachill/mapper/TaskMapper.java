package ru.gigachill.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.gigachill.dto.TaskDTO;
import ru.gigachill.dto.TaskWithShoppingListsDTO;
import ru.gigachill.web.api.model.Task;
import ru.gigachill.web.api.model.TaskStatus;
import ru.gigachill.web.api.model.TaskWithShoppingLists;

@Mapper(
        componentModel = "spring",
        uses = {ShoppingListMapper.class, UserMapper.class})
public interface TaskMapper {

    @Mapping(source = "status", target = "status", qualifiedByName = "stringToTaskStatus")
    @Mapping(target = "permissions", ignore = true)
    TaskWithShoppingLists toTaskWithShoppingLists(TaskWithShoppingListsDTO dto);

    @Mapping(source = "status", target = "status", qualifiedByName = "stringToTaskStatus")
    @Mapping(target = "permissions", ignore = true)
    Task toTask(TaskDTO dto);

    @Named("stringToTaskStatus")
    default TaskStatus stringToTaskStatus(String status) {
        return TaskStatus.fromValue(status);
    }
}
