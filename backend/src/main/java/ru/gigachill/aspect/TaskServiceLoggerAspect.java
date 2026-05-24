package ru.gigachill.aspect;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.gigachill.config.LoggerColorConfig;
import ru.gigachill.model.UserEntity;
import ru.gigachill.web.api.model.TaskReviewRequest;

@Component
@Aspect
@RequiredArgsConstructor
public class TaskServiceLoggerAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceLoggerAspect.class);
    private final LoggerColorConfig loggerColorConfig;

    @Pointcut(
            "execution(public * ru.gigachill.service.TaskService.getAllTasksFromEvent(..)) "
                    + "&& args(eventId, ..)")
    public void getAllTasksFromEvent(UUID eventId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.TaskService.getTaskById(..)) "
                    + "&& args(taskId, ..)")
    public void getTaskById(UUID taskId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.TaskService.createTask(..)) "
                    + "&& args(eventId, userEntity, ..)")
    public void createTask(UUID eventId, UserEntity userEntity) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.TaskService.updateTask(..)) "
                    + "&& args(eventId, taskId, ..)")
    public void updateTask(UUID eventId, UUID taskId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.TaskService.startExecuting(..)) "
                    + "&& args(taskId, userId, ..)")
    public void startExecuting(UUID taskId, UUID userId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.TaskService.deleteTask(..)) "
                    + "&& args( taskId)")
    public void deleteTask(UUID taskId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.TaskService.isAuthor(..)) "
                    + "&& args(taskId, userId)")
    public void isAuthor(UUID taskId, UUID userId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.TaskService.getTaskStatus(..)) "
                    + "&& args(taskId)")
    public void getTaskStatus(UUID taskId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.TaskService.isExisted(..)) "
                    + "&& args(eventID, taskId)")
    public void isExisted(UUID eventID, UUID taskId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.TaskService.getExecutorId(..)) "
                    + "&& args(taskId)")
    public void getExecutorId(UUID taskId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.TaskService.updateExecutor(..)) "
                    + "&& args(taskId, ..)")
    public void updateExecutor(UUID taskId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.TaskService.updateShoppingLists(..)) "
                    + "&& args(taskId,..)")
    public void updateShoppingLists(UUID taskId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.TaskService.setExecutorComment(..)) "
                    + "&& args(taskId, ..)")
    public void setExecutorComment(UUID taskId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.TaskService.setReviewerComment(..)) "
                    + "&& args(taskId, taskReviewRequest, ..)")
    public void setReviewerComment(UUID taskId, TaskReviewRequest taskReviewRequest) {}

    @Around("getAllTasksFromEvent(eventId)")
    public Object logGetAllTasksFromEvent(ProceedingJoinPoint proceedingJoinPoint, UUID eventId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Event tasks with id: {} received{}",
                loggerColorConfig.getGET_COLOR(),
                loggerColorConfig.getGET_LABEL(),
                eventId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("getTaskById(taskId)")
    public Object logGetTaskById(ProceedingJoinPoint proceedingJoinPoint, UUID taskId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Task with id: {} received{}",
                loggerColorConfig.getGET_COLOR(),
                loggerColorConfig.getGET_LABEL(),
                taskId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("createTask(eventId, userEntity)")
    public Object logCreateTask(
            ProceedingJoinPoint proceedingJoinPoint, UUID eventId, UserEntity userEntity)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}User with id: {} created task with id: {} in event with id: {}{}",
                loggerColorConfig.getPOST_COLOR(),
                loggerColorConfig.getPOST_LABEL(),
                userEntity.getId(),
                result,
                eventId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("updateTask(eventId, taskId)")
    public Object logUpdateTask(ProceedingJoinPoint proceedingJoinPoint, UUID eventId, UUID taskId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Task with id: {} was updated{}",
                loggerColorConfig.getPATCH_COLOR(),
                loggerColorConfig.getPATCH_LABEL(),
                taskId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("startExecuting(taskId, userId)")
    public Object logStartExecuting(
            ProceedingJoinPoint proceedingJoinPoint, UUID taskId, UUID userId) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}User with id: {} started execution task with id: {}{}",
                loggerColorConfig.getPOST_COLOR(),
                loggerColorConfig.getPOST_LABEL(),
                userId,
                taskId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("deleteTask(taskId)")
    public Object logDeleteTask(ProceedingJoinPoint proceedingJoinPoint, UUID taskId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Task with id: {} was deleted{}",
                loggerColorConfig.getDELETE_COLOR(),
                loggerColorConfig.getDELETE_LABEL(),
                taskId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("isAuthor(taskId, userId)")
    public Object logIsAuthor(ProceedingJoinPoint proceedingJoinPoint, UUID taskId, UUID userId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        if ((Boolean) result) {
            LOGGER.info(
                    "{}{}User with id: {} is author of task with id: {}{}",
                    loggerColorConfig.getGET_COLOR(),
                    loggerColorConfig.getGET_LABEL(),
                    userId,
                    taskId,
                    loggerColorConfig.getRESET_COLOR());
        } else {
            LOGGER.info(
                    "{}{}User with id: {} is not author of task with id: {}{}",
                    loggerColorConfig.getGET_COLOR(),
                    loggerColorConfig.getGET_LABEL(),
                    userId,
                    taskId,
                    loggerColorConfig.getRESET_COLOR());
        }
        return result;
    }

    @Around("getTaskStatus(taskId)")
    public Object logGetTaskStatus(ProceedingJoinPoint proceedingJoinPoint, UUID taskId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Task with id: {} has status {}{}",
                loggerColorConfig.getGET_COLOR(),
                loggerColorConfig.getGET_LABEL(),
                taskId,
                result,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("isExisted(eventID, taskId)")
    public Object logIsExisted(ProceedingJoinPoint proceedingJoinPoint, UUID eventID, UUID taskId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        if ((Boolean) result) {
            LOGGER.info(
                    "{}{}Task with id: {} is existed in event with id: {}{}",
                    loggerColorConfig.getGET_COLOR(),
                    loggerColorConfig.getGET_LABEL(),
                    taskId,
                    eventID,
                    loggerColorConfig.getRESET_COLOR());
        } else {
            LOGGER.info(
                    "{}{}Task with id: {} is not existed in event with id: {}{}",
                    loggerColorConfig.getGET_COLOR(),
                    loggerColorConfig.getGET_LABEL(),
                    taskId,
                    eventID,
                    loggerColorConfig.getRESET_COLOR());
        }
        return result;
    }

    @Around("getExecutorId(taskId)")
    public Object logGetExecutorId(ProceedingJoinPoint proceedingJoinPoint, UUID taskId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        if (result == null) {
            LOGGER.info(
                    "{}{}Task with id: {} does not have executor{}",
                    loggerColorConfig.getGET_COLOR(),
                    loggerColorConfig.getGET_LABEL(),
                    taskId,
                    loggerColorConfig.getRESET_COLOR());
        } else {
            LOGGER.info(
                    "{}{}Task with id: {} has executor with id: {}{}",
                    loggerColorConfig.getGET_COLOR(),
                    loggerColorConfig.getGET_LABEL(),
                    taskId,
                    result,
                    loggerColorConfig.getRESET_COLOR());
        }
        return result;
    }

    @Around("updateExecutor(taskId)")
    public Object logUpdateExecutor(ProceedingJoinPoint proceedingJoinPoint, UUID taskId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        if (result == null) {
            LOGGER.info(
                    "{}{}Task with id: {} no longer has an executor{}",
                    loggerColorConfig.getPUT_COLOR(),
                    loggerColorConfig.getPUT_LABEL(),
                    taskId,
                    loggerColorConfig.getRESET_COLOR());
        } else {
            LOGGER.info(
                    "{}{}Task with id: {} now has an executor with id: {}{}",
                    loggerColorConfig.getPUT_COLOR(),
                    loggerColorConfig.getPUT_LABEL(),
                    taskId,
                    result,
                    loggerColorConfig.getRESET_COLOR());
        }
        return result;
    }

    @Around("updateShoppingLists(taskId)")
    public Object logUpdateShoppingLists(ProceedingJoinPoint proceedingJoinPoint, UUID taskId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Shopping lists in task with id: {} was updated{}",
                loggerColorConfig.getPUT_COLOR(),
                loggerColorConfig.getPUT_LABEL(),
                taskId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("setExecutorComment(taskId)")
    public Object logSetExecutorComment(ProceedingJoinPoint proceedingJoinPoint, UUID taskId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Task with id: {} received a comment from the executor: {}{}",
                loggerColorConfig.getPOST_COLOR(),
                loggerColorConfig.getPOST_LABEL(),
                taskId,
                result,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("setReviewerComment(taskId, body)")
    public Object logSetReviewerComment(
            ProceedingJoinPoint proceedingJoinPoint,
            UUID taskId,
            TaskReviewRequest taskReviewRequest)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        var reviewerComment = taskReviewRequest.getReviewerComment();
        var isApproved = taskReviewRequest.getIsApproved();
        if (isApproved) {
            LOGGER.info(
                    "{}{}Task with id: {} was confirmed with a reviewer comment: {}{}",
                    loggerColorConfig.getPOST_COLOR(),
                    loggerColorConfig.getPOST_LABEL(),
                    taskId,
                    reviewerComment,
                    loggerColorConfig.getRESET_COLOR());
        } else {
            LOGGER.info(
                    "{}{}Task with id: {} was rejected with a reviewer comment: {}{}",
                    loggerColorConfig.getPOST_COLOR(),
                    loggerColorConfig.getPOST_LABEL(),
                    taskId,
                    reviewerComment,
                    loggerColorConfig.getRESET_COLOR());
        }
        return result;
    }
}
