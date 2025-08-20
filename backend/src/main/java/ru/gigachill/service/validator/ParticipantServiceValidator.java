package ru.gigachill.service.validator;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import ru.gigachill.repository.composite.ParticipantDAO;
import ru.gigachill.repository.composite.ShoppingListDAO;
import ru.gigachill.repository.composite.TaskDAO;
import ru.gigachill.exception.BadRequestException;
import ru.gigachill.exception.ConflictException;
import ru.gigachill.exception.ForbiddenException;

@Component
@RequiredArgsConstructor
public class ParticipantServiceValidator {
    private final ParticipantDAO participantDAO;
    private final ShoppingListDAO shoppingListDAO;
    private final TaskDAO taskDAO;
    private final Environment env;

    public void checkUserInEvent(UUID eventId, UUID userId) {
        if (!participantDAO.checkUserInEvent(eventId, userId)) {
            throw new ForbiddenException(
                    "User with id: "
                            + userId
                            + " is not a participant of event with id: "
                            + eventId);
        }
    }

    public void checkIsAlreadyParticipant(UUID eventId, UUID userId) {
        if (participantDAO.checkUserInEvent(eventId, userId)) {
            throw new ConflictException(
                    "User with id "
                            + userId
                            + " is already participant of event with id "
                            + eventId);
        }
    }

    public void checkAdminOrOwnerRole(UUID eventId, UUID participantId) {
        if (isParticipantRole(eventId, participantId)) {
            throw new ForbiddenException(
                    "User with id: "
                            + participantId
                            + " does not have Admin or Owner role in event with id: "
                            + eventId);
        }
    }

    public void checkOwnerRole(UUID eventId, UUID participantId) {
        if (!(isOwnerRole(eventId, participantId))) {
            throw new ForbiddenException(
                    "User with id: "
                            + participantId
                            + " does not have Owner role in event with id: "
                            + eventId);
        }
    }

    public void checkReplaceRole(UUID eventId, UUID participantId) {
        if (isOwnerRole(eventId, participantId)) {
            throw new ConflictException(
                    "The role: owner of the user with id: "
                            + participantId
                            + " cannot be replaced");
        }
    }

    public void checkIsConsumerOrAdminOrOwner(
            UUID eventId, UUID participantId, UUID shoppingListId) {
        if (isParticipantRole(eventId, participantId)
                && !shoppingListDAO.isConsumer(shoppingListId, participantId)) {
            throw new ForbiddenException(
                    "User with id: "
                            + participantId
                            + " is not Admin/Owner or a consumer of shopping list with id: "
                            + shoppingListId);
        }
    }

    public void checkIsAuthorOrAdminOrOwner(UUID eventId, UUID participantId, UUID taskId) {
        if (isParticipantRole(eventId, participantId) && !taskDAO.isAuthor(taskId, participantId)) {
            throw new ForbiddenException(
                    "User with id: "
                            + participantId
                            + " is not Admin/Owner or a author of task with id: "
                            + taskId);
        }
    }

    public void checkIsSamePerson(UUID userId, UUID participantId) {
        if (userId.equals(participantId)) {
            throw new BadRequestException(
                    "User with id: " + participantId + " perform this action on themselves");
        }
    }

    public boolean isOwnerRole(UUID eventId, UUID participantId) {
        return participantDAO
                .getParticipantRoleInEvent(eventId, participantId)
                .equals(env.getProperty("roles.owner"));
    }

    public boolean isAdminRole(UUID eventId, UUID participantId) {
        return participantDAO
                .getParticipantRoleInEvent(eventId, participantId)
                .equals(env.getProperty("roles.admin"));
    }

    public boolean isParticipantRole(UUID eventId, UUID participantId) {
        return participantDAO
                .getParticipantRoleInEvent(eventId, participantId)
                .equals(env.getProperty("roles.participant"));
    }
}
