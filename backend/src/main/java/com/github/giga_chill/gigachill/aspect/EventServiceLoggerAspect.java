package com.github.giga_chill.gigachill.aspect;


import com.github.giga_chill.gigachill.model.Event;
import com.github.giga_chill.gigachill.web.info.RequestEventInfo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.aspectj.lang.annotation.Aspect;


@Component
@Aspect
public class EventServiceLoggerAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventServiceLoggerAspect.class);
    private static final String POST_COLOR = "\u001b[32m";
    private static final String GET_COLOR = "\u001b[36m";
    private static final String DELETE_COLOR = "\u001b[31m";
    private static final String PATCH_COLOR = "\u001b[35m";
    private static final String RESET_COLOR = "\u001B[0m";

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.EventService.createEvent(..)) " +
            "&& args(userId, requestEventInfo)")
    public void createEvent(String userId, RequestEventInfo requestEventInfo) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.EventService.getAllUserEvents(..)) " +
            "&& args(userId)")
    public void getAllUserEvents(String userId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.EventService.getEventById(..)) " +
            "&& args(eventId)")
    public void getEventById(String eventId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.EventService.deleteEvent(..)) " +
            "&& args(eventId, userId)")
    public void deleteEvent(String eventId, String userId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.EventService.updateEvent(..)) " +
            "&& args(eventId, requestEventInfo)")
    public void updateEvent(String eventId, RequestEventInfo requestEventInfo) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.EventService.isExisted(..)) " +
            "&& args(eventId)")
    public void isExisted(String eventId) {
    }


    @Around("createEvent(userId, requestEventInfo)")
    public Object logCreateEvent(ProceedingJoinPoint proceedingJoinPoint,
                                 String userId, RequestEventInfo requestEventInfo) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(POST_COLOR + "User with id: {} created event with id: {}" + RESET_COLOR, userId, ((String) result));
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("getAllUserEvents(userId)")
    public Object logGetAllUserEvents(ProceedingJoinPoint proceedingJoinPoint,
                                      String userId) throws Throwable {

        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(GET_COLOR + "User with id: {} got all his events" + RESET_COLOR, userId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("getEventById(eventId)")
    public Object logGetEventById(ProceedingJoinPoint proceedingJoinPoint,
                                  String eventId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(GET_COLOR + "Information about the event with id: {} received" + RESET_COLOR, eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("deleteEvent(eventId, userId)")
    public Object logDeleteEvent(ProceedingJoinPoint proceedingJoinPoint,
                                 String eventId, String userId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(DELETE_COLOR + "Event with id: {} has been deleted" + RESET_COLOR, eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("updateEvent(eventId, requestEventInfo)")
    public Object logUpdateEvent(ProceedingJoinPoint proceedingJoinPoint,
                                 String eventId, RequestEventInfo requestEventInfo) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(PATCH_COLOR + "Event with id: {} has been changed" + RESET_COLOR, eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("isExisted(eventId)")
    public Object logIsExisted(ProceedingJoinPoint proceedingJoinPoint, String eventId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            if ((Boolean) result) {
                LOGGER.info(GET_COLOR + "Event with id: {} exists" + RESET_COLOR, eventId);
            } else {
                LOGGER.info(GET_COLOR + "Event with id: {} does not exist" + RESET_COLOR, eventId);
            }
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }


}
