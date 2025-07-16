package com.github.giga_chill.gigachill.aspect;

import com.github.giga_chill.gigachill.config.LoggerColorConfig;
import com.github.giga_chill.gigachill.model.User;
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
public class ParticipantsServiceLoggerAspect {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ParticipantsServiceLoggerAspect.class);
    private final LoggerColorConfig loggerColorConfig;

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ParticipantsService.getAllParticipantsByEventId(..)) "
                    + "&& args(eventId)")
    public void getAllParticipantsByEventId(UUID eventId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ParticipantsService.addParticipantToEvent(..)) "
                    + "&& args(eventId, user)")
    public void addParticipantToEvent(UUID eventId, User user) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ParticipantsService.deleteParticipant(..)) "
                    + "&& args(eventId, participantId)")
    public void deleteParticipant(UUID eventId, UUID participantId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ParticipantsService.isParticipant(..)) "
                    + "&& args(eventId, userId)")
    public void isParticipant(UUID eventId, UUID userId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ParticipantsService.updateParticipantRole(..)) "
                    + "&& args(eventId, participantId, role)")
    public void updateParticipantRole(UUID eventId, UUID participantId, String role) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ParticipantsService.getParticipantRoleInEvent(..)) "
                    + "&& args(eventId, participantId)")
    public void getParticipantRoleInEvent(UUID eventId, UUID participantId) {}

    @Pointcut(
            "execution(public * com.github.giga_chill.gigachill.service.ParticipantsService.getParticipantById(..)) "
                    + "&& args(eventId, participantId)")
    public void getParticipantById(UUID eventId, UUID participantId) {}

    @Around("getAllParticipantsByEventId(eventId)")
    public Object logGetAllParticipantsByEventId(
            ProceedingJoinPoint proceedingJoinPoint, UUID eventId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getGET_COLOR()
                            + "Event participants with: {} id received"
                            + loggerColorConfig.getRESET_COLOR(),
                    eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("getParticipantById(eventId, participantId)")
    public Object logGetParticipantById(
            ProceedingJoinPoint proceedingJoinPoint, UUID eventId, UUID participantId)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getGET_COLOR()
                            + "Participant with id: {} from event with id: {} received"
                            + loggerColorConfig.getRESET_COLOR(),
                    participantId,
                    eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("addParticipantToEvent(eventId, user)")
    public Object logAddParticipantToEvent(
            ProceedingJoinPoint proceedingJoinPoint, UUID eventId, User user) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getPOST_COLOR()
                            + "User with id: {} was added to event with id: {}"
                            + loggerColorConfig.getRESET_COLOR(),
                    user.getId(),
                    eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("deleteParticipant(eventId, participantId)")
    public Object logDeleteParticipant(
            ProceedingJoinPoint proceedingJoinPoint, UUID eventId, UUID participantId)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getDELETE_COLOR()
                            + "User with id: {} was deleted from event with id: {}"
                            + loggerColorConfig.getRESET_COLOR(),
                    participantId,
                    eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("isParticipant(eventId, userId)")
    public Object logIsParticipant(
            ProceedingJoinPoint proceedingJoinPoint, UUID eventId, UUID userId) throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            if ((Boolean) result) {
                LOGGER.info(
                        loggerColorConfig.getGET_COLOR()
                                + "User with id: {} is a participant of the event with id: {}"
                                + loggerColorConfig.getRESET_COLOR(),
                        userId,
                        eventId);
            } else {
                LOGGER.info(
                        loggerColorConfig.getGET_COLOR()
                                + "User with id: {} is not a participant of the event with id: {}"
                                + loggerColorConfig.getRESET_COLOR(),
                        userId,
                        eventId);
            }
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("updateParticipantRole(eventId, participantId, role)")
    public Object logUpdateParticipantRole(
            ProceedingJoinPoint proceedingJoinPoint, UUID eventId, UUID participantId, String role)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getPATCH_COLOR()
                            + "User with id: {} got role: {} in the event with id: {}"
                            + loggerColorConfig.getRESET_COLOR(),
                    participantId,
                    role,
                    eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }

    @Around("getParticipantRoleInEvent(eventId, participantId)")
    public Object logGetParticipantRoleInEvent(
            ProceedingJoinPoint proceedingJoinPoint, UUID eventId, UUID participantId)
            throws Throwable {
        try {
            Object result = proceedingJoinPoint.proceed();
            LOGGER.info(
                    loggerColorConfig.getPATCH_COLOR()
                            + "User with id: {} has role: {} in the event with id: {}"
                            + loggerColorConfig.getRESET_COLOR(),
                    participantId,
                    (String) result,
                    eventId);
            return result;
        } catch (Throwable ex) {
            throw ex;
        }
    }
}
