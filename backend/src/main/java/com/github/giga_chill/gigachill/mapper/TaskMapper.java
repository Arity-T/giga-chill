package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.TaskDTO;
import com.github.giga_chill.gigachill.data.transfer.object.TaskWithShoppingListsDTO;
import com.github.giga_chill.gigachill.web.info.ResponseTaskInfo;
import com.github.giga_chill.gigachill.web.info.ResponseTaskWithShoppingListsInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {ShoppingListMapper.class, UserMapper.class, UuidMapper.class})
public interface TaskMapper {
    @Mapping(source = "taskId", target = "taskId", qualifiedByName = "uuidToString")
    ResponseTaskInfo toResponseTaskInfo(TaskDTO dto);

    @Mapping(source = "taskId", target = "taskId", qualifiedByName = "uuidToString")
    ResponseTaskWithShoppingListsInfo toResponseTaskWithShoppingListsInfo(
            TaskWithShoppingListsDTO dto);
}
