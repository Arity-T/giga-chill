package com.github.giga_chill.gigachill.service;


import com.github.giga_chill.gigachill.exception.NotFoundException;
import com.github.giga_chill.gigachill.model.Task;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.util.UuidUtils;
import com.github.giga_chill.gigachill.web.info.RequestTaskInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final Environment env;
    private final ShoppingListsService shoppingListsService;

    //TEMPORARY
    private final Map<UUID, Map<UUID, Task>> TASKS = new HashMap<>();


    public List<Task> getAllTasksFromEvent(UUID eventId){
        //TODO: связь с бд

        //TEMPORARY
        return TASKS.get(eventId).values().stream().toList();
    }

    public String createTask(UUID eventId, User user, RequestTaskInfo requestTaskInfo){
        //TODO: связь с бд
        List<UUID> shoppingListsIds = requestTaskInfo.shopping_lists_ids().stream()
                .map(UuidUtils::safeUUID).toList();

        if(shoppingListsService.areExisted(shoppingListsIds)){
            throw new NotFoundException("One or more of the resources involved were not found: "
                    + requestTaskInfo.shopping_lists_ids());
        }

        Task task = new Task(UUID.randomUUID(), requestTaskInfo.title(), requestTaskInfo.description(),
                env.getProperty("task_status.open"), requestTaskInfo.deadline_datetime(),
                null, user, null,
                shoppingListsService.getShoppingListsByIds(shoppingListsIds));

        //TEMPORARY
        Map<UUID, Task> eventTasks = TASKS.get(eventId);
        if (eventTasks == null) {
            eventTasks = new HashMap<>();
            TASKS.put(eventId, eventTasks);
        }
        eventTasks.put(task.getTaskId(), task);

        return task.getTaskId().toString();
    }

}
