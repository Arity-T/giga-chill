package ru.gigachill.aspect;

import java.util.List;
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

@Component
@Aspect
@RequiredArgsConstructor
public class ShoppingListServiceLoggerAspect {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ShoppingListServiceLoggerAspect.class);
    private final LoggerColorConfig loggerColorConfig;

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListService.getAllShoppingListsFromEvent(..)) "
                    + "&& args(eventId, ..)")
    public void getAllShoppingListsFromEvent(UUID eventId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListService.getShoppingListById(..)) "
                    + "&& args(shoppingListId)")
    public void getShoppingListById(UUID shoppingListId) {}

    @Pointcut("execution(public * ru.gigachill.service.ShoppingListService.createShoppingList(..))")
    public void createShoppingList() {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListService.updateShoppingList(..)) "
                    + "&& args(eventId, userId, shoppingListId, ..)")
    public void updateShoppingList(UUID eventId, UUID userId, UUID shoppingListId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListService.deleteShoppingList(..)) "
                    + "&& args(shoppingListId, ..)")
    public void deleteShoppingList(UUID shoppingListId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListService.addShoppingItem(..)) "
                    + "&& args(shoppingListId, ..)")
    public void addShoppingItem(UUID shoppingListId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListService.updateShoppingItem(..)) "
                    + "&& args(shoppingItemId, ..)")
    public void updateShoppingItem(UUID shoppingItemId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListService.deleteShoppingItemFromShoppingList(..)) "
                    + "&& args(shoppingListId, shoppingItemId, ..)")
    public void deleteShoppingItemFromShoppingList(UUID shoppingListId, UUID shoppingItemId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListService.updateShoppingItemStatus(..)) "
                    + "&& args(shoppingItemId, ..)")
    public void updateShoppingItemStatus(UUID shoppingItemId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListService.getShoppingItemById(..)) "
                    + "&& args(shoppingItemId)")
    public void getShoppingItemById(UUID shoppingItemId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListService.updateShoppingListConsumers(..)) "
                    + "&& args(shoppingListId, ..)")
    public void updateShoppingListConsumers(UUID shoppingListId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListService.getShoppingListStatus(..)) "
                    + "&& args(shoppingListId)")
    public void getShoppingListStatus(UUID shoppingListId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListService.isExisted(..)) "
                    + "&& args(shoppingListId)")
    public void isExisted(UUID shoppingListId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListService.isConsumer(..)) "
                    + "&& args(shoppingListId, consumerId)")
    public void isConsumer(UUID shoppingListId, UUID consumerId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListService.isShoppingItemExisted(..)) "
                    + "&& args(shoppingItemId)")
    public void isShoppingItemExisted(UUID shoppingItemId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListService.getShoppingListsByIds(..)) "
                    + "&& args(shoppingListsIds)")
    public void getShoppingListsByIds(List<UUID> shoppingListsIds) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListService.areExisted(..)) "
                    + "&& args(shoppingListsIds)")
    public void areExisted(List<UUID> shoppingListsIds) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListService.canBindShoppingListsToTask(..)) "
                    + "&& args(shoppingListsIds, ..)")
    public void canBindShoppingListsToTask(List<UUID> shoppingListsIds) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListService.setBudget(..)) "
                    + "&& args(shoppingListId, ..)")
    public void setBudget(UUID shoppingListId) {}

    @Around("getAllShoppingListsFromEvent(eventId)")
    public Object logGetAllShoppingListsFromEvent(
            ProceedingJoinPoint proceedingJoinPoint, UUID eventId) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Event shopping lists with id: {} received{}",
                loggerColorConfig.getGET_COLOR(),
                loggerColorConfig.getGET_LABEL(),
                eventId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("getShoppingListById(shoppingListId)")
    public Object logGetShoppingListById(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingListId) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Shopping list with id: {} received{}",
                loggerColorConfig.getGET_COLOR(),
                loggerColorConfig.getGET_LABEL(),
                shoppingListId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("createShoppingList()")
    public Object logCreateShoppingList(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Shopping list with id: {} was created{}",
                loggerColorConfig.getPOST_COLOR(),
                loggerColorConfig.getPOST_LABEL(),
                result,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("updateShoppingList(eventId, userId, shoppingListId)")
    public Object logUpdateShoppingList(
            ProceedingJoinPoint proceedingJoinPoint, UUID eventId, UUID userId, UUID shoppingListId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Shopping list with id: {} was updated{}",
                loggerColorConfig.getPATCH_COLOR(),
                loggerColorConfig.getPATCH_LABEL(),
                shoppingListId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("deleteShoppingList(shoppingListId)")
    public Object logDeleteShoppingList(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingListId) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Shopping list with id: {} was deleted{}",
                loggerColorConfig.getDELETE_COLOR(),
                loggerColorConfig.getDELETE_LABEL(),
                shoppingListId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("addShoppingItem(shoppingListId)")
    public Object logAddShoppingItem(ProceedingJoinPoint proceedingJoinPoint, UUID shoppingListId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Shopping item with id: {} was added to shopping list with id: {}{}",
                loggerColorConfig.getPOST_COLOR(),
                loggerColorConfig.getPOST_LABEL(),
                result,
                shoppingListId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("updateShoppingItem(shoppingItemId)")
    public Object logUpdateShoppingItem(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingItemId) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Shopping item with id: {} was updated{}",
                loggerColorConfig.getPATCH_COLOR(),
                loggerColorConfig.getPATCH_LABEL(),
                shoppingItemId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("deleteShoppingItemFromShoppingList(shoppingListId, shoppingItemId)")
    public Object logDeleteShoppingItemFromShoppingList(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingListId, UUID shoppingItemId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Shopping item with id: {} was deleted from shopping list with id: {}{}",
                loggerColorConfig.getDELETE_COLOR(),
                loggerColorConfig.getDELETE_LABEL(),
                shoppingItemId,
                shoppingListId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("updateShoppingItemStatus(shoppingItemId)")
    public Object logUpdateShoppingItemStatus(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingItemId) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Shopping item with id: {} was bought: {}{}",
                loggerColorConfig.getPATCH_COLOR(),
                loggerColorConfig.getPATCH_LABEL(),
                shoppingItemId,
                result,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("getShoppingItemById(shoppingItemId)")
    public Object logGetShoppingItemById(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingItemId) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Shopping item with id: {} was received{}",
                loggerColorConfig.getGET_COLOR(),
                loggerColorConfig.getGET_LABEL(),
                shoppingItemId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("updateShoppingListConsumers(shoppingListId)")
    public Object logUpdateShoppingListConsumers(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingListId) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Shopping list with id: {} consumers was updated{}",
                loggerColorConfig.getPUT_COLOR(),
                loggerColorConfig.getPUT_LABEL(),
                shoppingListId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("getShoppingListStatus(shoppingListId)")
    public Object logGetShoppingListStatus(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingListId) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Shopping list with id: {} has status {}{}",
                loggerColorConfig.getGET_COLOR(),
                loggerColorConfig.getGET_LABEL(),
                shoppingListId,
                result,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("isExisted(shoppingListId)")
    public Object logIsExisted(ProceedingJoinPoint proceedingJoinPoint, UUID shoppingListId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        if ((Boolean) result) {
            LOGGER.info(
                    "{}{}Shopping list with id: {} exists{}",
                    loggerColorConfig.getGET_COLOR(),
                    loggerColorConfig.getGET_LABEL(),
                    shoppingListId,
                    loggerColorConfig.getRESET_COLOR());
        } else {
            LOGGER.info(
                    "{}{}Shopping list with id: {} does not exist{}",
                    loggerColorConfig.getGET_COLOR(),
                    loggerColorConfig.getGET_LABEL(),
                    shoppingListId,
                    loggerColorConfig.getRESET_COLOR());
        }
        return result;
    }

    @Around("isConsumer(shoppingListId, consumerId)")
    public Object logIsConsumer(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingListId, UUID consumerId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        if ((Boolean) result) {
            LOGGER.info(
                    "{}{}Participant with with id: {} is consumer of the shopping list with id: {}{}",
                    loggerColorConfig.getGET_COLOR(),
                    loggerColorConfig.getGET_LABEL(),
                    consumerId,
                    shoppingListId,
                    loggerColorConfig.getRESET_COLOR());
        } else {
            LOGGER.info(
                    "{}{}Participant with with id: {} is not consumer of the shopping list with id: {}{}",
                    loggerColorConfig.getGET_COLOR(),
                    loggerColorConfig.getGET_LABEL(),
                    consumerId,
                    shoppingListId,
                    loggerColorConfig.getRESET_COLOR());
        }
        return result;
    }

    @Around("isShoppingItemExisted(shoppingItemId)")
    public Object logIsShoppingItemExisted(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingItemId) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        if ((Boolean) result) {
            LOGGER.info(
                    "{}{}Shopping item with id: {} exists{}",
                    loggerColorConfig.getGET_COLOR(),
                    loggerColorConfig.getGET_LABEL(),
                    shoppingItemId,
                    loggerColorConfig.getRESET_COLOR());
        } else {
            LOGGER.info(
                    "{}{}Shopping item with id: {} does not exist{}",
                    loggerColorConfig.getGET_COLOR(),
                    loggerColorConfig.getGET_LABEL(),
                    shoppingItemId,
                    loggerColorConfig.getRESET_COLOR());
        }
        return result;
    }

    @Around("getShoppingListsByIds(shoppingListsIds)")
    public Object logGetShoppingListsByIds(
            ProceedingJoinPoint proceedingJoinPoint, List<UUID> shoppingListsIds) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Shopping lists with ids: {} received{}",
                loggerColorConfig.getGET_COLOR(),
                loggerColorConfig.getGET_LABEL(),
                shoppingListsIds.toString(),
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("areExisted(shoppingListsIds)")
    public Object logAreExisted(
            ProceedingJoinPoint proceedingJoinPoint, List<UUID> shoppingListsIds) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        if ((Boolean) result) {
            LOGGER.info(
                    "{}{}Shopping lists with ids: {} exist{}",
                    loggerColorConfig.getGET_COLOR(),
                    loggerColorConfig.getGET_LABEL(),
                    shoppingListsIds.toString(),
                    loggerColorConfig.getRESET_COLOR());
        } else {
            LOGGER.info(
                    "{}{}Shopping lists with ids: {} do not exist{}",
                    loggerColorConfig.getGET_COLOR(),
                    loggerColorConfig.getGET_LABEL(),
                    shoppingListsIds.toString(),
                    loggerColorConfig.getRESET_COLOR());
        }
        return result;
    }

    @Around("canBindShoppingListsToTask(shoppingListsIds)")
    public Object logCanBindShoppingListsToTask(
            ProceedingJoinPoint proceedingJoinPoint, List<UUID> shoppingListsIds) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        if ((Boolean) result) {
            LOGGER.info(
                    "{}{}Shopping lists with ids: {} can bind to task{}",
                    loggerColorConfig.getGET_COLOR(),
                    loggerColorConfig.getGET_LABEL(),
                    shoppingListsIds.toString(),
                    loggerColorConfig.getRESET_COLOR());
        } else {
            LOGGER.info(
                    "{}{}Shopping lists with ids: {} can not bind to task{}",
                    loggerColorConfig.getGET_COLOR(),
                    loggerColorConfig.getGET_LABEL(),
                    shoppingListsIds.toString(),
                    loggerColorConfig.getRESET_COLOR());
        }
        return result;
    }

    @Around("setBudget(shoppingListId)")
    public Object logSetBudget(ProceedingJoinPoint proceedingJoinPoint, UUID shoppingListId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Shopping list with id: {} received a new budget {}{}",
                loggerColorConfig.getPUT_COLOR(),
                loggerColorConfig.getPUT_LABEL(),
                shoppingListId,
                result,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }
}
