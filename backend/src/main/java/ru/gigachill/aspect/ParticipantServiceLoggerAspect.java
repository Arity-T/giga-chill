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
import ru.gigachill.web.api.model.ParticipantSetRole;

@Component
@Aspect
@RequiredArgsConstructor
public class ParticipantServiceLoggerAspect {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(ParticipantServiceLoggerAspect.class);
    private final LoggerColorConfig loggerColorConfig;

    @Pointcut(
            "execution(public * ru.gigachill.service.ParticipantService.getAllParticipantsByEventId(..)) "
                    + "&& args(eventId, ..)")
    public void getAllParticipantsByEventId(UUID eventId) {}

    @Pointcut(
            value =
                    "execution(public * ru.gigachill.service.ParticipantService.addParticipantToEvent(..)) "
                            + "&& args(eventId, ..)")
    public void addParticipantToEvent(UUID eventId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ParticipantService.deleteParticipant(..)) "
                    + "&& args(eventId, participantId, ..)")
    public void deleteParticipant(UUID eventId, UUID participantId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ParticipantService.isParticipant(..)) "
                    + "&& args(eventId, userId)")
    public void isParticipant(UUID eventId, UUID userId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ParticipantService.updateParticipantRole(..)) "
                    + "&& args(eventId, userId, participantId, participantSetRole)")
    public void updateParticipantRole(
            UUID eventId, UUID userId, UUID participantId, ParticipantSetRole participantSetRole) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ParticipantService.getParticipantRoleInEvent(..)) "
                    + "&& args(eventId, participantId)")
    public void getParticipantRoleInEvent(UUID eventId, UUID participantId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ParticipantService.getParticipantById(..)) "
                    + "&& args(eventId, participantId)")
    public void getParticipantById(UUID eventId, UUID participantId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ParticipantService.getParticipantBalance(..)) "
                    + "&& args(eventId, participantId)")
    public void getParticipantBalance(UUID eventId, UUID participantId) {}

    @Pointcut(
            "execution(public * ru.gigachill.service.ParticipantService.getParticipantsSummaryBalance(..)) "
                    + "&& args(eventId, ..)")
    public void getParticipantsSummaryBalance(UUID eventId) {}

    @Around("getAllParticipantsByEventId(eventId)")
    public Object logGetAllParticipantsByEventId(
            ProceedingJoinPoint proceedingJoinPoint, UUID eventId) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Event participants with: {} id received{}",
                loggerColorConfig.getGET_COLOR(),
                loggerColorConfig.getGET_LABEL(),
                eventId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("getParticipantById(eventId, participantId)")
    public Object logGetParticipantById(
            ProceedingJoinPoint proceedingJoinPoint, UUID eventId, UUID participantId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}Participant with id: {} from event with id: {} received{}",
                loggerColorConfig.getGET_COLOR(),
                loggerColorConfig.getGET_LABEL(),
                participantId,
                eventId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("addParticipantToEvent(eventId)")
    public Object logAddParticipantToEvent(ProceedingJoinPoint proceedingJoinPoint, UUID eventId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}User with id: {} was added to event with id: {}{}",
                loggerColorConfig.getPOST_COLOR(),
                loggerColorConfig.getPOST_LABEL(),
                result,
                eventId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("deleteParticipant(eventId, participantId)")
    public Object logDeleteParticipant(
            ProceedingJoinPoint proceedingJoinPoint, UUID eventId, UUID participantId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}User with id: {} was deleted from event with id: {}{}",
                loggerColorConfig.getDELETE_COLOR(),
                loggerColorConfig.getDELETE_LABEL(),
                participantId,
                eventId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("isParticipant(eventId, userId)")
    public Object logIsParticipant(
            ProceedingJoinPoint proceedingJoinPoint, UUID eventId, UUID userId) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        if ((Boolean) result) {
            LOGGER.info(
                    "{}{}User with id: {} is a participant of the event with id: {}{}",
                    loggerColorConfig.getGET_COLOR(),
                    loggerColorConfig.getGET_LABEL(),
                    userId,
                    eventId,
                    loggerColorConfig.getRESET_COLOR());
        } else {
            LOGGER.info(
                    "{}User with id: {} is not a participant of the event with id: {}{}",
                    loggerColorConfig.getGET_COLOR(),
                    userId,
                    eventId,
                    loggerColorConfig.getRESET_COLOR());
        }
        return result;
    }

    @Around("updateParticipantRole(eventId, userId, participantId, participantSetRole)")
    public Object logUpdateParticipantRole(
            ProceedingJoinPoint proceedingJoinPoint,
            UUID eventId,
            UUID userId,
            UUID participantId,
            ParticipantSetRole participantSetRole)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        var role = participantSetRole.getRole().getValue();
        LOGGER.info(
                "{}{}User with id: {} got role: {} in the event with id: {}{}",
                loggerColorConfig.getPATCH_COLOR(),
                loggerColorConfig.getPATCH_LABEL(),
                participantId,
                role,
                eventId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("getParticipantRoleInEvent(eventId, participantId)")
    public Object logGetParticipantRoleInEvent(
            ProceedingJoinPoint proceedingJoinPoint, UUID eventId, UUID participantId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}User with id: {} has role: {} in the event with id: {}{}",
                loggerColorConfig.getPATCH_COLOR(),
                loggerColorConfig.getPATCH_LABEL(),
                participantId,
                result,
                eventId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("getParticipantBalance(eventId, participantId)")
    public Object logGetParticipantBalance(
            ProceedingJoinPoint proceedingJoinPoint, UUID eventId, UUID participantId)
            throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}User with id: {} received balance in event with id: {}{}",
                loggerColorConfig.getGET_COLOR(),
                loggerColorConfig.getGET_LABEL(),
                participantId,
                eventId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }

    @Around("getParticipantsSummaryBalance(eventId)")
    public Object logGetParticipantsSummaryBalance(
            ProceedingJoinPoint proceedingJoinPoint, UUID eventId) throws Throwable {
        Object result = proceedingJoinPoint.proceed();
        LOGGER.info(
                "{}{}The summary balance of event with id: {} participants was received{}",
                loggerColorConfig.getGET_COLOR(),
                loggerColorConfig.getGET_LABEL(),
                eventId,
                loggerColorConfig.getRESET_COLOR());
        return result;
    }
}
