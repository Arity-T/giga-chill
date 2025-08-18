package ru.gigachill.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.gigachill.data.transfer.object.TaskDTO;
import ru.gigachill.data.transfer.object.TaskWithShoppingListsDTO;
import ru.gigachill.web.api.model.Task;
import ru.gigachill.web.api.model.TaskStatus;
import ru.gigachill.web.api.model.TaskWithShoppingLists;

@Mapper(
        componentModel = "spring",
        uses = {ShoppingListMapper.class, UserMapper.class})
public interface TaskMapper {

    @Mapping(source = "status", target = "status", qualifiedByName = "stringToTaskStatus")
    TaskWithShoppingLists toTaskWithShoppingLists(TaskWithShoppingListsDTO dto);

    @Mapping(source = "status", target = "status", qualifiedByName = "stringToTaskStatus")
    Task toTask(TaskDTO dto);

    @Named("stringToTaskStatus")
    default TaskStatus stringToTaskStatus(String status) {
        return TaskStatus.fromValue(status);
    }
}
