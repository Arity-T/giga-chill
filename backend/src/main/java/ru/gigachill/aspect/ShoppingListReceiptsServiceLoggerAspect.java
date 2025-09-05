package ru.gigachill.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.gigachill.config.LoggerColorConfig;

import java.util.UUID;

@Component
@Aspect
@RequiredArgsConstructor
public class ShoppingListReceiptsServiceLoggerAspect {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ShoppingListReceiptsServiceLoggerAspect.class);
    private final LoggerColorConfig loggerColorConfig;

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListReceiptsService.uploadPolicy(..)) "
                    + "&& args(userId, eventId, shoppingListId, ..)")
    public void uploadPolicy(UUID userId, UUID eventId, UUID shoppingListId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListReceiptsService.confirmUpload(..)) "
                    + "&& args(userId, eventId, shoppingListId, ..)")
    public void confirmUpload(UUID userId, UUID eventId, UUID shoppingListId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListReceiptsService.deleteReceipt(..)) "
                    + "&& args(userId, eventId, shoppingListId, receiptId)")
    public void deleteReceipt(UUID userId, UUID eventId, UUID shoppingListId, UUID receiptId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ShoppingListReceiptsService.getReceipt(..)) "
                    + "&& args(userId, eventId, shoppingListId, receiptId)")
    public void getReceipt(UUID userId, UUID eventId, UUID shoppingListId, UUID receiptId) {}

    @Around("uploadPolicy(userId, eventId, shoppingListId)")
    public Object logUploadPolicy(
            ProceedingJoinPoint proceedingJoinPoint, UUID userId, UUID eventId, UUID shoppingListId) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Policy for loading receipt for shopping list with id: {} was created{}",
                loggerColorConfig.getPOST_COLOR(),
                loggerColorConfig.getPOST_COLOR(),
                shoppingListId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("confirmUpload(userId, eventId, shoppingListId)")
    public Object logConfirmUpload(
            ProceedingJoinPoint proceedingJoinPoint, UUID userId, UUID eventId, UUID shoppingListId) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}The receipt for the shopping list with id: {} was uploaded to the file storage{}",
                loggerColorConfig.getPOST_COLOR(),
                loggerColorConfig.getPOST_COLOR(),
                shoppingListId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("deleteReceipt(userId, eventId, shoppingListId, receiptId)")
    public Object logDeleteReceipt(
            ProceedingJoinPoint proceedingJoinPoint, UUID userId, UUID eventId, UUID shoppingListId, UUID receiptId) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}The receipt with id: {} was deleted{}",
                loggerColorConfig.getDELETE_COLOR(),
                loggerColorConfig.getDELETE_COLOR(),
                receiptId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("getReceipt(userId, eventId, shoppingListId, receiptId)")
    public Object logGetReceipt(
            ProceedingJoinPoint proceedingJoinPoint, UUID userId, UUID eventId, UUID shoppingListId, UUID receiptId) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}The file with the receipt id: {} was received{}",
                loggerColorConfig.getGET_COLOR(),
                loggerColorConfig.getGET_COLOR(),
                receiptId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }
}
