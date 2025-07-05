package com.github.giga_chill.gigachill.aspect;

import com.github.giga_chill.gigachill.model.User;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class ParticipantsServiceLoggerAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParticipantsServiceLoggerAspect.class);
    private static final String POST_COLOR = "\u001b[32m";
    private static final String GET_COLOR = "\u001b[36m";
    private static final String DELETE_COLOR = "\u001b[31m";
    private static final String PATCH_COLOR = "\u001b[35m";
    private static final String RESET_COLOR = "\u001B[0m";

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.ParticipantsService.getAllParticipantsByEventId(..)) " +
            "&& args(eventId)")
    public void getAllParticipantsByEventId(String eventId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.ParticipantsService.addParticipantToEvent(..)) " +
            "&& args(eventId, user)")
    public void addParticipantToEvent(String eventId, User user) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.ParticipantsService.deleteParticipant(..)) " +
            "&& args(eventId, participantId)")
    public void deleteParticipant(String eventId, String participantId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.ParticipantsService.isParticipant(..)) " +
            "&& args(eventId, userId)")
    public void isParticipant(String eventId, String userId) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.ParticipantsService.updateParticipantRole(..)) " +
            "&& args(eventId, participantId, role)")
    public void updateParticipantRole(String eventId, String participantId, String role) {
    }

    @Pointcut("execution(public * com.github.giga_chill.gigachill.service.ParticipantsService.getParticipantRoleInEvent(..)) " +
            "&& args(eventId, participantId)")
    public void getParticipantRoleInEvent(String eventId, String participantId) {
    }


    @Around("getAllParticipantsByEventId(eventId)")
    public Object logGetAllParticipantsByEventId(ProceedingJoinPoint proceedingJoinPoint,
                                                 String eventId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(GET_COLOR + "Event participants with: {} id received" + RESET_COLOR, eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("addParticipantToEvent(eventId, user)")
    public Object logAddParticipantToEvent(ProceedingJoinPoint proceedingJoinPoint,
                                           String eventId, User user) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(POST_COLOR + "User with id: {} was added to event with id: {}" + RESET_COLOR, user.id, eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("deleteParticipant(eventId, participantId)")
    public Object logDeleteParticipant(ProceedingJoinPoint proceedingJoinPoint,
                                       String eventId, String participantId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(DELETE_COLOR + "User with id: {} was deleted from event with id: {}" + RESET_COLOR, participantId, eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("isParticipant(eventId, userId)")
    public Object logIsParticipant(ProceedingJoinPoint proceedingJoinPoint,
                                   String eventId, String userId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            if ((Boolean) result) {
                LOGGER.info(GET_COLOR + "User with id: {} is a participant of the event with id: {}" + RESET_COLOR,
                        userId, eventId);
            } else {
                LOGGER.info(GET_COLOR + "User with id: {} is not a participant of the event with id: {}" + RESET_COLOR,
                        userId, eventId);
            }
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("updateParticipantRole(eventId, participantId, role)")
    public Object logUpdateParticipantRole(ProceedingJoinPoint proceedingJoinPoint,
                                           String eventId, String participantId, String role) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(PATCH_COLOR + "User with id: {} got role: {} in the event with id: {}" + RESET_COLOR,
                    participantId, role, eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("getParticipantRoleInEvent(eventId, participantId)")
    public Object logGetParticipantRoleInEvent(ProceedingJoinPoint proceedingJoinPoint,
                                               String eventId, String participantId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(PATCH_COLOR + "User with id: {} has role: {} in the event with id: {}" + RESET_COLOR,
                    participantId, (String) result, eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }


}
