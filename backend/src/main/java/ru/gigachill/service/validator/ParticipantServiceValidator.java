package ru.gigachill.service.validator;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.gigachill.exception.BadRequestException;
import ru.gigachill.exception.ConflictException;
import ru.gigachill.exception.ForbiddenException;
import ru.gigachill.properties.RoleProperties;
import ru.gigachill.repository.composite.ParticipantCompositeRepository;
import ru.gigachill.repository.composite.ShoppingListCompositeRepository;
import ru.gigachill.repository.composite.TaskCompositeRepository;

@Component
@RequiredArgsConstructor
public class ParticipantServiceValidator {
    private final ParticipantCompositeRepository participantCompositeRepository;
    private final ShoppingListCompositeRepository shoppingListCompositeRepository;
    private final TaskCompositeRepository taskCompositeRepository;
    private final RoleProperties roleProperties;

    public void checkUserInEvent(UUID eventId, UUID userId) {
        if (!participantCompositeRepository.checkUserInEvent(eventId, userId)) {
            throw new ForbiddenException(
                    "User with id: "
                            + userId
                            + " is not a participant of event with id: "
                            + eventId);
        }
    }

    public void checkIsAlreadyParticipant(UUID eventId, UUID userId) {
        if (participantCompositeRepository.checkUserInEvent(eventId, userId)) {
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
                && !shoppingListCompositeRepository.isConsumer(shoppingListId, participantId)) {
            throw new ForbiddenException(
                    "User with id: "
                            + participantId
                            + " is not Admin/Owner or a consumer of shopping list with id: "
                            + shoppingListId);
        }
    }

    public void checkIsAuthorOrAdminOrOwner(UUID eventId, UUID participantId, UUID taskId) {
        if (isParticipantRole(eventId, participantId)
                && !taskCompositeRepository.isAuthor(taskId, participantId)) {
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
        return participantCompositeRepository
                .getParticipantRoleInEvent(eventId, participantId)
                .equals(roleProperties.getOwner());
    }

    public boolean isAdminRole(UUID eventId, UUID participantId) {
        return participantCompositeRepository
                .getParticipantRoleInEvent(eventId, participantId)
                .equals(roleProperties.getAdmin());
    }

    public boolean isParticipantRole(UUID eventId, UUID participantId) {
        return participantCompositeRepository
                .getParticipantRoleInEvent(eventId, participantId)
                .equals(roleProperties.getParticipant());
    }
}
