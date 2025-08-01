package com.github.giga_chill.gigachill.aspect;

import com.github.giga_chill.gigachill.config.LoggerColorConfig;
import java.math.BigDecimal;
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

@Component
@Aspect
@RequiredArgsConstructor
public class ShoppingListsServiceLoggerAspect {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ShoppingListsServiceLoggerAspect.class);
    private final LoggerColorConfig loggerColorConfig;

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.getAllShoppingListsFromEvent(..)) "
                    + "&& args(eventId, ..)")
    public void getAllShoppingListsFromEvent(UUID eventId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.getShoppingListById(..)) "
                    + "&& args(shoppingListId)")
    public void getShoppingListById(UUID shoppingListId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.createShoppingList(..))")
    public void createShoppingList() {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.updateShoppingList(..)) "
                    + "&& args(shoppingListId, ..)")
    public void updateShoppingList(UUID shoppingListId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.deleteShoppingList(..)) "
                    + "&& args(shoppingListId)")
    public void deleteShoppingList(UUID shoppingListId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.addShoppingItem(..)) "
                    + "&& args(shoppingListId, ..)")
    public void addShoppingItem(UUID shoppingListId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.updateShoppingItem(..)) "
                    + "&& args(shoppingItemId, ..)")
    public void updateShoppingItem(UUID shoppingItemId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.deleteShoppingItemFromShoppingList(..)) "
                    + "&& args(shoppingListId, shoppingItemId)")
    public void deleteShoppingItemFromShoppingList(UUID shoppingListId, UUID shoppingItemId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.updateShoppingItemStatus(..)) "
                    + "&& args(shoppingItemId, status)")
    public void updateShoppingItemStatus(UUID shoppingItemId, boolean status) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.getShoppingItemById(..)) "
                    + "&& args(shoppingItemId)")
    public void getShoppingItemById(UUID shoppingItemId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.updateShoppingListConsumers(..)) "
                    + "&& args(shoppingListId, ..)")
    public void updateShoppingListConsumers(UUID shoppingListId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.getShoppingListStatus(..)) "
                    + "&& args(shoppingListId)")
    public void getShoppingListStatus(UUID shoppingListId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.isExisted(..)) "
                    + "&& args(shoppingListId)")
    public void isExisted(UUID shoppingListId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.isConsumer(..)) "
                    + "&& args(shoppingListId, consumerId)")
    public void isConsumer(UUID shoppingListId, UUID consumerId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.isShoppingItemExisted(..)) "
                    + "&& args(shoppingItemId)")
    public void isShoppingItemExisted(UUID shoppingItemId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.getShoppingListsByIds(..)) "
                    + "&& args(shoppingListsIds)")
    public void getShoppingListsByIds(List<UUID> shoppingListsIds) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.areExisted(..)) "
                    + "&& args(shoppingListsIds)")
    public void areExisted(List<UUID> shoppingListsIds) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.canBindShoppingListsToTask(..)) "
                    + "&& args(shoppingListsIds, ..)")
    public void canBindShoppingListsToTask(List<UUID> shoppingListsIds) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.setBudget(..)) "
                    + "&& args(shoppingListId, budget)")
    public void setBudget(UUID shoppingListId, BigDecimal budget) {}

    @Around("getAllShoppingListsFromEvent(eventId)")
    public Object logGetAllShoppingListsFromEvent(
            ProceedingJoinPoint proceedingJoinPoint, UUID eventId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getGET_COLOR()
                            + loggerColorConfig.getGET_LABEL()
                            + "Event shopping lists with id: {} received"
                            + loggerColorConfig.getRESET_COLOR(),
                    eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("getShoppingListById(shoppingListId)")
    public Object logGetShoppingListById(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingListId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getGET_COLOR()
                            + loggerColorConfig.getGET_LABEL()
                            + "Shopping list with id: {} received"
                            + loggerColorConfig.getRESET_COLOR(),
                    shoppingListId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("createShoppingList()")
    public Object logCreateShoppingList(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getPOST_COLOR()
                            + loggerColorConfig.getPOST_LABEL()
                            + "Shopping list with id: {} was created"
                            + loggerColorConfig.getRESET_COLOR(),
                    (String) result);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("updateShoppingList(shoppingListId)")
    public Object logUpdateShoppingList(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingListId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getPATCH_COLOR()
                            + loggerColorConfig.getPATCH_LABEL()
                            + "Shopping list with id: {} was updated"
                            + loggerColorConfig.getRESET_COLOR(),
                    shoppingListId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("deleteShoppingList(shoppingListId)")
    public Object logDeleteShoppingList(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingListId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getDELETE_COLOR()
                            + loggerColorConfig.getDELETE_LABEL()
                            + "Shopping list with id: {} was deleted"
                            + loggerColorConfig.getRESET_COLOR(),
                    shoppingListId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("addShoppingItem(shoppingListId)")
    public Object logAddShoppingItem(ProceedingJoinPoint proceedingJoinPoint, UUID shoppingListId)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getPOST_COLOR()
                            + loggerColorConfig.getPOST_LABEL()
                            + "Shopping item with id: {} was added to shopping list with id: {}"
                            + loggerColorConfig.getRESET_COLOR(),
                    (String) result,
                    shoppingListId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("updateShoppingItem(shoppingItemId)")
    public Object logUpdateShoppingItem(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingItemId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getPATCH_COLOR()
                            + loggerColorConfig.getPATCH_LABEL()
                            + "Shopping item with id: {} was updated"
                            + loggerColorConfig.getRESET_COLOR(),
                    shoppingItemId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("deleteShoppingItemFromShoppingList(shoppingListId, shoppingItemId)")
    public Object logDeleteShoppingItemFromShoppingList(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingListId, UUID shoppingItemId)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getDELETE_COLOR()
                            + loggerColorConfig.getDELETE_LABEL()
                            + "Shopping item with id: {} was deleted from shopping list with id: {}"
                            + loggerColorConfig.getRESET_COLOR(),
                    shoppingItemId,
                    shoppingListId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("updateShoppingItemStatus(shoppingItemId, status)")
    public Object logUpdateShoppingItemStatus(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingItemId, boolean status)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getPATCH_COLOR()
                            + loggerColorConfig.getPATCH_LABEL()
                            + "Shopping item with id: {} was bought: {}"
                            + loggerColorConfig.getRESET_COLOR(),
                    shoppingItemId,
                    status);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("getShoppingItemById(shoppingItemId)")
    public Object logGetShoppingItemById(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingItemId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getGET_COLOR()
                            + loggerColorConfig.getGET_LABEL()
                            + "Shopping item with id: {} was received"
                            + loggerColorConfig.getRESET_COLOR(),
                    shoppingItemId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("updateShoppingListConsumers(shoppingListId)")
    public Object logUpdateShoppingListConsumers(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingListId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getPUT_COLOR()
                            + loggerColorConfig.getPUT_LABEL()
                            + "Shopping list with id: {} consumers was updated"
                            + loggerColorConfig.getRESET_COLOR(),
                    shoppingListId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("getShoppingListStatus(shoppingListId)")
    public Object logGetShoppingListStatus(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingListId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getGET_COLOR()
                            + loggerColorConfig.getGET_LABEL()
                            + "Shopping list with id: {} has status {}"
                            + loggerColorConfig.getRESET_COLOR(),
                    shoppingListId,
                    (String) result);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("isExisted(shoppingListId)")
    public Object logIsExisted(ProceedingJoinPoint proceedingJoinPoint, UUID shoppingListId)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            if ((Boolean) result) {
                LOGGER.info(
                        loggerColorConfig.getGET_COLOR()
                                + loggerColorConfig.getGET_LABEL()
                                + "Shopping list with id: {} exists"
                                + loggerColorConfig.getRESET_COLOR(),
                        shoppingListId);
            } else {
                LOGGER.info(
                        loggerColorConfig.getGET_COLOR()
                                + loggerColorConfig.getGET_LABEL()
                                + "Shopping list with id: {} does not exist"
                                + loggerColorConfig.getRESET_COLOR(),
                        shoppingListId);
            }
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("isConsumer(shoppingListId, consumerId)")
    public Object logIsConsumer(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingListId, UUID consumerId)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            if ((Boolean) result) {
                LOGGER.info(
                        loggerColorConfig.getGET_COLOR()
                                + loggerColorConfig.getGET_LABEL()
                                + "Participant with with id: {} is consumer of "
                                + "the shopping list with id: {}"
                                + loggerColorConfig.getRESET_COLOR(),
                        consumerId,
                        shoppingListId);
            } else {
                LOGGER.info(
                        loggerColorConfig.getGET_COLOR()
                                + loggerColorConfig.getGET_LABEL()
                                + "Participant with with id: {} is not consumer of "
                                + "the shopping list with id: {}"
                                + loggerColorConfig.getRESET_COLOR(),
                        consumerId,
                        shoppingListId);
            }
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("isShoppingItemExisted(shoppingItemId)")
    public Object logIsShoppingItemExisted(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingItemId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            if ((Boolean) result) {
                LOGGER.info(
                        loggerColorConfig.getGET_COLOR()
                                + loggerColorConfig.getGET_LABEL()
                                + "Shopping item with id: {} exists"
                                + loggerColorConfig.getRESET_COLOR(),
                        shoppingItemId);
            } else {
                LOGGER.info(
                        loggerColorConfig.getGET_COLOR()
                                + loggerColorConfig.getGET_LABEL()
                                + "Shopping item with id: {} does not exist"
                                + loggerColorConfig.getRESET_COLOR(),
                        shoppingItemId);
            }
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("getShoppingListsByIds(shoppingListsIds)")
    public Object logGetShoppingListsByIds(
            ProceedingJoinPoint proceedingJoinPoint, List<UUID> shoppingListsIds) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getGET_COLOR()
                            + loggerColorConfig.getGET_LABEL()
                            + "Shopping lists with ids: {} received"
                            + loggerColorConfig.getRESET_COLOR(),
                    shoppingListsIds.toString());
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("areExisted(shoppingListsIds)")
    public Object logAreExisted(
            ProceedingJoinPoint proceedingJoinPoint, List<UUID> shoppingListsIds) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            if ((Boolean) result) {
                LOGGER.info(
                        loggerColorConfig.getGET_COLOR()
                                + loggerColorConfig.getGET_LABEL()
                                + "Shopping lists with ids: {} exist"
                                + loggerColorConfig.getRESET_COLOR(),
                        shoppingListsIds.toString());
            } else {
                LOGGER.info(
                        loggerColorConfig.getGET_COLOR()
                                + loggerColorConfig.getGET_LABEL()
                                + "Shopping lists with ids: {} do not exist"
                                + loggerColorConfig.getRESET_COLOR(),
                        shoppingListsIds.toString());
            }
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("canBindShoppingListsToTask(shoppingListsIds, ..)")
    public Object logCanBindShoppingListsToTask(
            ProceedingJoinPoint proceedingJoinPoint, List<UUID> shoppingListsIds) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            if ((Boolean) result) {
                LOGGER.info(
                        loggerColorConfig.getGET_COLOR()
                                + loggerColorConfig.getGET_LABEL()
                                + "Shopping lists with ids: {} can bind to task"
                                + loggerColorConfig.getRESET_COLOR(),
                        shoppingListsIds.toString());
            } else {
                LOGGER.info(
                        loggerColorConfig.getGET_COLOR()
                                + loggerColorConfig.getGET_LABEL()
                                + "Shopping lists with ids: {} can not bind to task"
                                + loggerColorConfig.getRESET_COLOR(),
                        shoppingListsIds.toString());
            }
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("setBudget(shoppingListId, budget)")
    public Object logSetBudget(
            ProceedingJoinPoint proceedingJoinPoint, UUID shoppingListId, BigDecimal budget)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getPUT_COLOR()
                            + loggerColorConfig.getPUT_LABEL()
                            + "Shopping list with id: {} received a new budget {}"
                            + loggerColorConfig.getRESET_COLOR(),
                    shoppingListId,
                    budget);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }
}
