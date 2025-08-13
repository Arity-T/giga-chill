package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.TaskDTO;
import com.github.giga_chill.gigachill.data.transfer.object.TaskWithShoppingListsDTO;
import com.github.giga_chill.gigachill.web.api.model.Task;
import com.github.giga_chill.gigachill.web.api.model.TaskStatus;
import com.github.giga_chill.gigachill.web.api.model.TaskWithShoppingLists;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(
        componentModel = "spring",
        uses = {ShoppingListMapper.class, UserMapper.class, UuidMapper.class})
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
