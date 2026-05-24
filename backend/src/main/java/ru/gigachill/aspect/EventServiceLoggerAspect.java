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
import ru.gigachill.web.api.model.EventId;

@Component
@Aspect
@RequiredArgsConstructor
public class EventServiceLoggerAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceLoggerAspect.class);
    private final LoggerColorConfig loggerColorConfig;

    @Pointcut(
            "execution(public * ru.gigachill.service.EventService.createEvent(..)) "
                    + "&& args(userId, ..)")
    public void createEvent(UUID userId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.EventService.getAllUserEvents(..)) "
                    + "&& args(userId)")
    public void getAllUserEvents(UUID userId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.EventService.getEventById(..)) "
                    + "&& args(userId, eventId)")
    public void getEventById(UUID userId, UUID eventId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.EventService.deleteEvent(..)) "
                    + "&& args(eventId, ..)")
    public void deleteEvent(UUID eventId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.EventService.updateEvent(..)) "
                    + "&& args(eventId, ..)")
    public void updateEvent(UUID eventId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.EventService.createInviteLink(..)) "
                    + "&& args(eventId, ..)")
    public void createInviteLink(UUID eventId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.EventService.getInviteLink(..)) "
                    + "&& args(eventId, ..)")
    public void getInviteLink(UUID eventId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.EventService.getEventByLinkUuid(..)) "
                    + "&& args(linkUuid)")
    public void getEventByLinkUuid(UUID linkUuid) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.EventService.joinByLink(..)) "
                    + "&& args(userEntity, ..)")
    public void joinByLink(UserEntity userEntity) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.EventService.getEndDatetime(..)) "
                    + "&& args(eventId)")
    public void getEndDatetime(UUID eventId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.EventService.finalizeEvent(..)) "
                    + "&& args(eventId, ..)")
    public void finalizeEvent(UUID eventId) {}

    @Around("createEvent(userId)")
    public Object logCreateEvent(ProceedingJoinPoint proceedingJoinPoint, UUID userId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}User with id: {} created event with id: {}{}",
                loggerColorConfig.getPOST_COLOR(),
                loggerColorConfig.getPOST_LABEL(),
                userId,
                result,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("getAllUserEvents(userId)")
    public Object logGetAllUserEvents(ProceedingJoinPoint proceedingJoinPoint, UUID userId)
            throws Throwable {

        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}User with id: {} got all his events{}",
                loggerColorConfig.getGET_COLOR(),
                loggerColorConfig.getGET_LABEL(),
                userId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("getEventById(userId, eventId)")
    public Object logGetEventById(
            ProceedingJoinPoint proceedingJoinPoint, UUID userId, UUID eventId) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Information about the event with id: {} received{}",
                loggerColorConfig.getGET_COLOR(),
                loggerColorConfig.getGET_LABEL(),
                eventId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("deleteEvent(eventId)")
    public Object logDeleteEvent(ProceedingJoinPoint proceedingJoinPoint, UUID eventId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Event with id: {} has been deleted{}",
                loggerColorConfig.getDELETE_COLOR(),
                loggerColorConfig.getDELETE_LABEL(),
                eventId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("updateEvent(eventId)")
    public Object logUpdateEvent(ProceedingJoinPoint proceedingJoinPoint, UUID eventId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Event with id: {} has been changed{}",
                loggerColorConfig.getPATCH_COLOR(),
                loggerColorConfig.getPATCH_LABEL(),
                eventId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("createInviteLink(eventId)")
    public Object logCreateInviteLink(ProceedingJoinPoint proceedingJoinPoint, UUID eventId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Event with id: {} has received a new invite link with hash: {}{}",
                loggerColorConfig.getPOST_COLOR(),
                loggerColorConfig.getPOST_LABEL(),
                eventId,
                result,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("getInviteLink(eventId)")
    public Object logGetInviteLink(ProceedingJoinPoint proceedingJoinPoint, UUID eventId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}The invite link to the event with id: {} was received.{}",
                loggerColorConfig.getGET_COLOR(),
                loggerColorConfig.getGET_LABEL(),
                eventId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("getEventByLinkUuid(linkUuid)")
    public Object logGetEventByLinkUuid(ProceedingJoinPoint proceedingJoinPoint, UUID linkUuid)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        if (result == null) {
            LOGGER.info(
                    "{}{}Invite link with hash: {} did not attach to event{}",
                    loggerColorConfig.getGET_COLOR(),
                    loggerColorConfig.getGET_LABEL(),
                    linkUuid,
                    loggerColorConfig.getRESET_COLOR());
        } else {
            LOGGER.info(
                    "{}{}Invite link with hash: {} attached to event with id: {}{}",
                    loggerColorConfig.getGET_COLOR(),
                    loggerColorConfig.getGET_LABEL(),
                    linkUuid,
                    result,
                    loggerColorConfig.getRESET_COLOR());
        }
        return result;
    }

    @Around("joinByLink(userEntity)")
    public Object logJoinByLink(ProceedingJoinPoint proceedingJoinPoint, UserEntity userEntity)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}User with id: {} joined event with id: {} via a link{}",
                loggerColorConfig.getPOST_COLOR(),
                loggerColorConfig.getPOST_LABEL(),
                userEntity.getId(),
                ((EventId) result).getEventId(),
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("getEndDatetime(eventId)")
    public Object logGetEndDatetime(ProceedingJoinPoint proceedingJoinPoint, UUID eventId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Event with id: {} has an end time: {}{}",
                loggerColorConfig.getGET_COLOR(),
                loggerColorConfig.getGET_LABEL(),
                eventId,
                result,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("finalizeEvent(eventId)")
    public Object logFinalizeEvent(ProceedingJoinPoint proceedingJoinPoint, UUID eventId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Event with id: {} was finalized{}",
                loggerColorConfig.getPOST_COLOR(),
                loggerColorConfig.getPOST_LABEL(),
                eventId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }
}
