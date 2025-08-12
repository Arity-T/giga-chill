package com.github.giga_chill.gigachill.aspect;

import com.github.giga_chill.gigachill.config.LoggerColorConfig;
import com.github.giga_chill.gigachill.model.UserEntity;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class TaskServiceLoggerAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskServiceLoggerAspect.class);
    private final LoggerColorConfig loggerColorConfig;

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.TaskService.getAllTasksFromEvent(..)) "
                    + "&& args(eventId, ..)")
    public void getAllTasksFromEvent(UUID eventId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.TaskService.getTaskById(..)) "
                    + "&& args(taskId, ..)")
    public void getTaskById(UUID taskId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.TaskService.createTask(..)) "
                    + "&& args(eventId, user, ..)")
    public void createTask(UUID eventId, UserEntity userEntity) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.TaskService.updateTask(..)) "
                    + "&& args(eventId, taskId, ..)")
    public void updateTask(UUID eventId, UUID taskId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.TaskService.startExecuting(..)) "
                    + "&& args(taskId, userId, ..)")
    public void startExecuting(UUID taskId, UUID userId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.TaskService.deleteTask(..)) "
                    + "&& args( taskId)")
    public void deleteTask(UUID taskId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.TaskService.isAuthor(..)) "
                    + "&& args(taskId, userId)")
    public void isAuthor(UUID taskId, UUID userId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.TaskService.getTaskStatus(..)) "
                    + "&& args(taskId)")
    public void getTaskStatus(UUID taskId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.TaskService.isExisted(..)) "
                    + "&& args(eventID, taskId)")
    public void isExisted(UUID eventID, UUID taskId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.TaskService.getExecutorId(..)) "
                    + "&& args(taskId)")
    public void getExecutorId(UUID taskId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.TaskService.updateExecutor(..)) "
                    + "&& args(taskId, ..)")
    public void updateExecutor(UUID taskId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.TaskService.updateShoppingLists(..)) "
                    + "&& args(taskId,..)")
    public void updateShoppingLists(UUID taskId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.TaskService.setExecutorComment(..)) "
                    + "&& args(taskId, ..)")
    public void setExecutorComment(UUID taskId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.TaskService.setReviewerComment(..)) "
                    + "&& args(taskId, body, ..)")
    public void setReviewerComment(UUID taskId, Map<String, Object> body) {}

    @Around("getAllTasksFromEvent(eventId)")
    public Object logGetAllTasksFromEvent(ProceedingJoinPoint proceedingJoinPoint, UUID eventId)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getGET_COLOR()
                            + loggerColorConfig.getGET_LABEL()
                            + "Event tasks with id: {} received"
                            + loggerColorConfig.getRESET_COLOR(),
                    eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("getTaskById(taskId)")
    public Object logGetTaskById(ProceedingJoinPoint proceedingJoinPoint, UUID taskId)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getGET_COLOR()
                            + loggerColorConfig.getGET_LABEL()
                            + "Task with id: {} received"
                            + loggerColorConfig.getRESET_COLOR(),
                    taskId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("createTask(eventId, user)")
    public Object logCreateTask(
            ProceedingJoinPoint proceedingJoinPoint, UUID eventId, UserEntity userEntity)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getPOST_COLOR()
                            + loggerColorConfig.getPOST_LABEL()
                            + "User with id: {} created task with id: "
                            + "{} in event with id: {}"
                            + loggerColorConfig.getRESET_COLOR(),
                    userEntity.getId(),
                    (String) result,
                    eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("updateTask(eventId, taskId)")
    public Object logUpdateTask(ProceedingJoinPoint proceedingJoinPoint, UUID eventId, UUID taskId)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getPATCH_COLOR()
                            + loggerColorConfig.getPATCH_LABEL()
                            + "Task with id: {} was updated"
                            + loggerColorConfig.getRESET_COLOR(),
                    taskId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("startExecuting(taskId, userId, ..)")
    public Object logStartExecuting(
            ProceedingJoinPoint proceedingJoinPoint, UUID taskId, UUID userId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getPOST_COLOR()
                            + loggerColorConfig.getPOST_LABEL()
                            + "User with id: {} started execution task with id: {}"
                            + loggerColorConfig.getRESET_COLOR(),
                    userId,
                    taskId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("deleteTask(taskId)")
    public Object logDeleteTask(ProceedingJoinPoint proceedingJoinPoint, UUID taskId)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getDELETE_COLOR()
                            + loggerColorConfig.getDELETE_LABEL()
                            + "Task with id: {} was deleted"
                            + loggerColorConfig.getRESET_COLOR(),
                    taskId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("isAuthor(taskId, userId)")
    public Object logIsAuthor(ProceedingJoinPoint proceedingJoinPoint, UUID taskId, UUID userId)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            if ((Boolean) result) {
                LOGGER.info(
                        loggerColorConfig.getGET_COLOR()
                                + loggerColorConfig.getGET_LABEL()
                                + "User with id: {} is author of task with id: {}"
                                + loggerColorConfig.getRESET_COLOR(),
                        userId,
                        taskId);
            } else {
                LOGGER.info(
                        loggerColorConfig.getGET_COLOR()
                                + loggerColorConfig.getGET_LABEL()
                                + "User with id: {} is not author of task with id: {}"
                                + loggerColorConfig.getRESET_COLOR(),
                        userId,
                        taskId);
            }
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("getTaskStatus(taskId)")
    public Object logGetTaskStatus(ProceedingJoinPoint proceedingJoinPoint, UUID taskId)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getGET_COLOR()
                            + loggerColorConfig.getGET_LABEL()
                            + "Task with id: {} has status {}"
                            + loggerColorConfig.getRESET_COLOR(),
                    taskId,
                    (String) result);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("isExisted(eventID, taskId)")
    public Object logIsExisted(ProceedingJoinPoint proceedingJoinPoint, UUID eventID, UUID taskId)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            if ((Boolean) result) {
                LOGGER.info(
                        loggerColorConfig.getGET_COLOR()
                                + loggerColorConfig.getGET_LABEL()
                                + "Task with id: {} is existed in event with id: {}"
                                + loggerColorConfig.getRESET_COLOR(),
                        taskId,
                        eventID);
            } else {
                LOGGER.info(
                        loggerColorConfig.getGET_COLOR()
                                + loggerColorConfig.getGET_LABEL()
                                + "Task with id: {} is not existed in event with id: {}"
                                + loggerColorConfig.getRESET_COLOR(),
                        taskId,
                        eventID);
            }
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("getExecutorId(taskId)")
    public Object logGetExecutorId(ProceedingJoinPoint proceedingJoinPoint, UUID taskId)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            if ((UUID) result == null) {
                LOGGER.info(
                        loggerColorConfig.getGET_COLOR()
                                + loggerColorConfig.getGET_LABEL()
                                + "Task with id: {} does not have executor"
                                + loggerColorConfig.getRESET_COLOR(),
                        taskId);
            } else {
                LOGGER.info(
                        loggerColorConfig.getGET_COLOR()
                                + loggerColorConfig.getGET_LABEL()
                                + "Task with id: {} has executor with id: {}"
                                + loggerColorConfig.getRESET_COLOR(),
                        taskId,
                        (UUID) result);
            }
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("updateExecutor(taskId, ..)")
    public Object logUpdateExecutor(ProceedingJoinPoint proceedingJoinPoint, UUID taskId)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            if (result == null) {
                LOGGER.info(
                        loggerColorConfig.getPUT_COLOR()
                                + loggerColorConfig.getPUT_LABEL()
                                + "Task with id: {} no longer has an executor"
                                + loggerColorConfig.getRESET_COLOR(),
                        taskId);
            } else {
                LOGGER.info(
                        loggerColorConfig.getPUT_COLOR()
                                + loggerColorConfig.getPUT_LABEL()
                                + "Task with id: {} now has an executor with id: {}"
                                + loggerColorConfig.getRESET_COLOR(),
                        taskId,
                        (UUID) result);
            }
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("updateShoppingLists(taskId, ..)")
    public Object logUpdateShoppingLists(ProceedingJoinPoint proceedingJoinPoint, UUID taskId)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getPUT_COLOR()
                            + loggerColorConfig.getPUT_LABEL()
                            + "Shopping lists in task with id: {} was updated"
                            + loggerColorConfig.getRESET_COLOR(),
                    taskId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("setExecutorComment(taskId, ..)")
    public Object logSetExecutorComment(ProceedingJoinPoint proceedingJoinPoint, UUID taskId)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getPOST_COLOR()
                            + loggerColorConfig.getPOST_LABEL()
                            + "Task with id: {} received a comment from the executor: {}"
                            + loggerColorConfig.getRESET_COLOR(),
                    taskId,
                    (String) result);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("setReviewerComment(taskId, body)")
    public Object logSetReviewerComment(
            ProceedingJoinPoint proceedingJoinPoint, UUID taskId, Map<String, Object> body)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            var reviewerComment = (String) body.get("reviewer_comment");
            var isApproved = (Boolean) body.get("is_approved");
            if (isApproved) {
                LOGGER.info(
                        loggerColorConfig.getPOST_COLOR()
                                + loggerColorConfig.getPOST_LABEL()
                                + "Task with id: {} was confirmed with a reviewer comment: {}"
                                + loggerColorConfig.getRESET_COLOR(),
                        taskId,
                        reviewerComment);
            } else {
                LOGGER.info(
                        loggerColorConfig.getPOST_COLOR()
                                + loggerColorConfig.getPOST_LABEL()
                                + "Task with id: {} was rejected with a reviewer comment: {}"
                                + loggerColorConfig.getRESET_COLOR(),
                        taskId,
                        reviewerComment);
            }
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }
}
