package com.github.giga_chill.gigachill.aspect;

import com.github.giga_chill.gigachill.config.LoggerColorConfig;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingListsServiceLoggerAspect.class);
    private final LoggerColorConfig loggerColorConfig;


    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.getAllShoppingListsFromEvent(..)) " +
            "&& args(eventId)")
    public void getAllShoppingListsFromEvent(String eventId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.getShoppingListById(..)) " +
            "&& args(eventId, shoppingListId)")
    public void getShoppingListById(String eventId, String shoppingListId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.createShoppingList(..))")
    public void createShoppingList() {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.updateShoppingList(..)) " +
            "&& args(eventId, shoppingListId, ..)")
    public void updateShoppingList(String eventId, String shoppingListId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.deleteShoppingList(..)) " +
            "&& args(eventId, shoppingListId)")
    public void deleteShoppingList(String eventId, String shoppingListId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.addShoppingItem(..)) " +
            "&& args(eventId, shoppingListId, ..)")
    public void addShoppingItem(String eventId, String shoppingListId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.updateShoppingItem(..)) " +
            "&& args(eventId, shoppingListId, shoppingItemId, ..)")
    public void updateShoppingItem(String eventId, String shoppingListId, String shoppingItemId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.deleteShoppingItemFromShoppingList(..)) " +
            "&& args(eventId, shoppingListId, shoppingItemId)")
    public void deleteShoppingItemFromShoppingList(String eventId, String shoppingListId, String shoppingItemId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.updateShoppingItemStatus(..)) " +
            "&& args(eventId, shoppingListId, shoppingItemId, status)")
    public void updateShoppingItemStatus(String eventId, String shoppingListId, String shoppingItemId,
                                         boolean status) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.getShoppingItemById(..)) " +
            "&& args(eventId, shoppingListId, shoppingItemId)")
    public void getShoppingItemById(String eventId, String shoppingListId,
                                    String shoppingItemId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.updateShoppingListConsumers(..)) " +
            "&& args(eventId, shoppingListId, ..)")
    public void updateShoppingListConsumers(String eventId, String shoppingListId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.getShoppingListStatus(..)) " +
            "&& args(eventId, shoppingListId)")
    public void getShoppingListStatus(String eventId, String shoppingListId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.isExisted(..)) " +
            "&& args(eventId, shoppingListId)")
    public void isExisted(String eventId, String shoppingListId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.isConsumer(..)) " +
            "&& args(eventId, shoppingListId, consumerId)")
    public void isConsumer(String eventId, String shoppingListId, String consumerId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.ShoppingListsService.isShoppingItemExisted(..)) " +
            "&& args(eventId, shoppingItemId)")
    public void isShoppingItemExisted(String eventId, String shoppingItemId) {
    }


    @Around("getAllShoppingListsFromEvent(eventId)")
    public Object logGetAllShoppingListsFromEvent(ProceedingJoinPoint proceedingJoinPoint,
                                                  String eventId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(loggerColorConfig.getGET_COLOR() + "Event shopping lists with id: {} received"
                    + loggerColorConfig.getRESET_COLOR(), eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("getShoppingListById(eventId, shoppingListId)")
    public Object logGetShoppingListById(ProceedingJoinPoint proceedingJoinPoint,
                                         String eventId,
                                         String shoppingListId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(loggerColorConfig.getGET_COLOR() + "Shopping list with id: {} received"
                    + loggerColorConfig.getRESET_COLOR(), shoppingListId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("createShoppingList()")
    public Object logCreateShoppingList(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(loggerColorConfig.getPOST_COLOR() + "Shopping list with id: {} was created"
                    + loggerColorConfig.getRESET_COLOR(), (String) result);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("updateShoppingList(eventId, shoppingListId)")
    public Object logUpdateShoppingList(ProceedingJoinPoint proceedingJoinPoint,
                                        String eventId,
                                        String shoppingListId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(loggerColorConfig.getPATCH_COLOR() + "Shopping list with id: {} was updated"
                    + loggerColorConfig.getRESET_COLOR(), shoppingListId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("deleteShoppingList(eventId, shoppingListId)")
    public Object logDeleteShoppingList(ProceedingJoinPoint proceedingJoinPoint,
                                        String eventId,
                                        String shoppingListId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(loggerColorConfig.getDELETE_COLOR() + "Shopping list with id: {} was deleted"
                    + loggerColorConfig.getRESET_COLOR(), shoppingListId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("addShoppingItem(eventId, shoppingListId)")
    public Object logAddShoppingItem(ProceedingJoinPoint proceedingJoinPoint,
                                     String eventId,
                                     String shoppingListId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(loggerColorConfig.getPOST_COLOR() + "Shopping item with id: {} was added to shopping list with id: {}"
                    + loggerColorConfig.getRESET_COLOR(), (String) result, shoppingListId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("updateShoppingItem(eventId, shoppingListId, shoppingItemId)")
    public Object logUpdateShoppingItem(ProceedingJoinPoint proceedingJoinPoint,
                                        String eventId,
                                        String shoppingListId,
                                        String shoppingItemId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(loggerColorConfig.getPATCH_COLOR() + "Shopping item with id: {} was updated"
                    + loggerColorConfig.getRESET_COLOR(), shoppingItemId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("deleteShoppingItemFromShoppingList(eventId, shoppingListId, shoppingItemId)")
    public Object logDeleteShoppingItemFromShoppingList(ProceedingJoinPoint proceedingJoinPoint,
                                                        String eventId,
                                                        String shoppingListId,
                                                        String shoppingItemId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(loggerColorConfig.getDELETE_COLOR() + "Shopping item with id: {} was deleted from shopping list with id: {}"
                    + loggerColorConfig.getRESET_COLOR(), shoppingItemId, shoppingListId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("updateShoppingItemStatus(eventId, shoppingListId, shoppingItemId, status)")
    public Object logUpdateShoppingItemStatus(ProceedingJoinPoint proceedingJoinPoint,
                                              String eventId,
                                              String shoppingListId,
                                              String shoppingItemId,
                                              boolean status) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(loggerColorConfig.getPATCH_COLOR() + "Shopping item status with id: {} is {} now"
                    + loggerColorConfig.getRESET_COLOR(), shoppingItemId, status);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }


    @Around("getShoppingItemById(eventId, shoppingListId, shoppingItemId)")
    public Object logGetShoppingItemById(ProceedingJoinPoint proceedingJoinPoint,
                                         String eventId,
                                         String shoppingListId,
                                         String shoppingItemId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(loggerColorConfig.getGET_COLOR() + "Shopping item with id: {} was received"
                    + loggerColorConfig.getRESET_COLOR(), shoppingItemId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("updateShoppingListConsumers(eventId, shoppingListId)")
    public Object logUpdateShoppingListConsumers(ProceedingJoinPoint proceedingJoinPoint,
                                                 String eventId,
                                                 String shoppingListId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(loggerColorConfig.getPUT_COLOR() + "Shopping list with id: {} consumers was updated"
                    + loggerColorConfig.getRESET_COLOR(), shoppingListId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("getShoppingListStatus(eventId, shoppingListId)")
    public Object logGetShoppingListStatus(ProceedingJoinPoint proceedingJoinPoint,
                                           String eventId,
                                           String shoppingListId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(loggerColorConfig.getGET_COLOR() + "Shopping list with id: {} has status {}"
                    + loggerColorConfig.getRESET_COLOR(), shoppingListId, (String) result);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("isExisted(eventId, shoppingListId)")
    public Object logIsExisted(ProceedingJoinPoint proceedingJoinPoint,
                               String eventId,
                               String shoppingListId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            if ((Boolean) result) {
                LOGGER.info(loggerColorConfig.getGET_COLOR() + "Shopping list with id: {} exists"
                                + loggerColorConfig.getRESET_COLOR(),
                        shoppingListId);
            } else {
                LOGGER.info(loggerColorConfig.getGET_COLOR() + "Shopping list with id: {} does not exist"
                                + loggerColorConfig.getRESET_COLOR(),
                        shoppingListId);
            }
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("isConsumer(eventId, shoppingListId, consumerId)")
    public Object logIsConsumer(ProceedingJoinPoint proceedingJoinPoint,
                                String eventId,
                                String shoppingListId,
                                String consumerId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            if ((Boolean) result) {
                LOGGER.info(loggerColorConfig.getGET_COLOR() + "Participant with with id: {} is consumer of " +
                                "the shopping list with id: {}"
                                + loggerColorConfig.getRESET_COLOR(),
                        consumerId, shoppingListId);
            } else {
                LOGGER.info(loggerColorConfig.getGET_COLOR() + "Participant with with id: {} is not consumer of " +
                                "the shopping list with id: {}"
                                + loggerColorConfig.getRESET_COLOR(),
                        consumerId, shoppingListId);
            }
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("isShoppingItemExisted(eventId, shoppingItemId)")
    public Object logIsShoppingItemExisted(ProceedingJoinPoint proceedingJoinPoint,
                                           String eventId,
                                           String shoppingItemId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            if ((Boolean) result) {
                LOGGER.info(loggerColorConfig.getGET_COLOR() + "Shopping item with id: {} exists"
                                + loggerColorConfig.getRESET_COLOR(),
                        shoppingItemId);
            } else {
                LOGGER.info(loggerColorConfig.getGET_COLOR() + "Shopping item with id: {} does not exist"
                                + loggerColorConfig.getRESET_COLOR(),
                        shoppingItemId);
            }
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }
}
