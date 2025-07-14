package com.github.giga_chill.gigachill.aspect;


import com.github.giga_chill.gigachill.config.LoggerColorConfig;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.web.info.RequestEventInfo;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Aspect;

import java.util.UUID;


@Component
@Aspect
@RequiredArgsConstructor
public class EventServiceLoggerAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceLoggerAspect.class);
    private final LoggerColorConfig loggerColorConfig;

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.EventService.createEvent(..)) " +
            "&& args(userId, requestEventInfo)")
    public void createEvent(UUID userId, RequestEventInfo requestEventInfo) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.EventService.getAllUserEvents(..)) " +
            "&& args(userId)")
    public void getAllUserEvents(UUID userId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.EventService.getEventById(..)) " +
            "&& args(eventId)")
    public void getEventById(UUID eventId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.EventService.deleteEvent(..)) " +
            "&& args(eventId, userId)")
    public void deleteEvent(UUID eventId, UUID userId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.EventService.updateEvent(..)) " +
            "&& args(eventId, requestEventInfo)")
    public void updateEvent(UUID eventId, RequestEventInfo requestEventInfo) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.EventService.isExisted(..)) " +
            "&& args(eventId)")
    public void isExisted(UUID eventId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.EventService.createInviteLink(..)) " +
            "&& args(eventId)")
    public void createInviteLink(UUID eventId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.EventService.getInviteLink(..)) " +
            "&& args(eventId)")
    public void getInviteLink(UUID eventId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.EventService.isCorrectLinkUuid(..)) " +
            "&& args(eventId, linkUuid)")
    public void isCorrectLinkUuid(UUID eventId, UUID linkUuid) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.EventService.joinByLink(..)) " +
            "&& args(eventId, user)")
    public void joinByLink(UUID eventId, User user) {
    }


    @Around("createEvent(userId, requestEventInfo)")
    public Object logCreateEvent(ProceedingJoinPoint proceedingJoinPoint,
                                 UUID userId, RequestEventInfo requestEventInfo) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(loggerColorConfig.getPOST_COLOR() + "User with id: {} created event with id: {}"
                    + loggerColorConfig.getRESET_COLOR(), userId, ((String) result));
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("getAllUserEvents(userId)")
    public Object logGetAllUserEvents(ProceedingJoinPoint proceedingJoinPoint,
                                      UUID userId) throws Throwable {

        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(loggerColorConfig.getGET_COLOR() + "User with id: {} got all his events"
                    + loggerColorConfig.getRESET_COLOR(), userId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("getEventById(eventId)")
    public Object logGetEventById(ProceedingJoinPoint proceedingJoinPoint,
                                  UUID eventId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(loggerColorConfig.getGET_COLOR() + "Information about the event with id: {} received"
                    + loggerColorConfig.getRESET_COLOR(), eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("deleteEvent(eventId, userId)")
    public Object logDeleteEvent(ProceedingJoinPoint proceedingJoinPoint,
                                 UUID eventId, UUID userId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(loggerColorConfig.getDELETE_COLOR() + "Event with id: {} has been deleted"
                    + loggerColorConfig.getRESET_COLOR(), eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("updateEvent(eventId, requestEventInfo)")
    public Object logUpdateEvent(ProceedingJoinPoint proceedingJoinPoint,
                                 UUID eventId, RequestEventInfo requestEventInfo) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(loggerColorConfig.getPATCH_COLOR() + "Event with id: {} has been changed"
                    + loggerColorConfig.getRESET_COLOR(), eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("isExisted(eventId)")
    public Object logIsExisted(ProceedingJoinPoint proceedingJoinPoint, UUID eventId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            if ((Boolean) result) {
                LOGGER.info(loggerColorConfig.getGET_COLOR() + "Event with id: {} exists"
                        + loggerColorConfig.getRESET_COLOR(), eventId);
            } else {
                LOGGER.info(loggerColorConfig.getGET_COLOR() + "Event with id: {} does not exist"
                        + loggerColorConfig.getRESET_COLOR(), eventId);
            }
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("createInviteLink(eventId)")
    public Object logCreateInviteLink(ProceedingJoinPoint proceedingJoinPoint,
                                      UUID eventId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(loggerColorConfig.getPOST_COLOR() + "Event with id: {} has received a new invite link with hash: {}"
                    + loggerColorConfig.getRESET_COLOR(), eventId, (String) result);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("getInviteLink(eventId)")
    public Object logGetInviteLink(ProceedingJoinPoint proceedingJoinPoint,
                                   UUID eventId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(loggerColorConfig.getGET_COLOR() + "The invite link to the event with id: {} was received."
                    + loggerColorConfig.getRESET_COLOR(), eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("isCorrectLinkUuid(eventId, linkUuid)")
    public Object logIsCorrectLinkUuid(ProceedingJoinPoint proceedingJoinPoint,
                                       UUID eventId,
                                       UUID linkUuid) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            if ((Boolean) result) {
                LOGGER.info(loggerColorConfig.getGET_COLOR() + "Event with id: {} contains invite link with hash: {}"
                        + loggerColorConfig.getRESET_COLOR(), eventId, linkUuid);
            } else {
                LOGGER.info(loggerColorConfig.getGET_COLOR() + "Event with id: {} does not contain invite link with hash: {}"
                        + loggerColorConfig.getRESET_COLOR(), eventId, linkUuid);
            }
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("joinByLink(eventId, user)")
    public Object logJoinByLink(ProceedingJoinPoint proceedingJoinPoint,
                                UUID eventId,
                                User user) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(loggerColorConfig.getPOST_COLOR() + "The user with id: {} joined event with id: {} via a link"
                    + loggerColorConfig.getRESET_COLOR(), user.getId(), eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

}
