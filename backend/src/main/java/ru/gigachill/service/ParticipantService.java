package ru.gigachill.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gigachill.dto.ParticipantDTO;
import ru.gigachill.jooq.generated.enums.EventRole;
import ru.gigachill.mapper.ParticipantBalanceMapper;
import ru.gigachill.mapper.ParticipantMapper;
import ru.gigachill.mapper.ParticipantSummaryBalanceMapper;
import ru.gigachill.model.UserEntity;
import ru.gigachill.properties.RoleProperties;
import ru.gigachill.repository.composite.ParticipantCompositeRepository;
import ru.gigachill.service.validator.EventServiceValidator;
import ru.gigachill.service.validator.ParticipantServiceValidator;
import ru.gigachill.web.api.model.*;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantBalanceMapper participantBalanceMapper;
    private final ParticipantSummaryBalanceMapper participantSummaryBalanceMapper;
    private final ParticipantMapper participantMapper;
    private final RoleProperties roleProperties;
    private final ParticipantCompositeRepository participantCompositeRepository;
    private final EventServiceValidator eventServiceValidator;
    private final ParticipantServiceValidator participantsServiceValidator;
    private final UserService userService;

    public List<Participant> getAllParticipantsByEventId(UUID eventId, UUID participantId) {

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        participantsServiceValidator.checkUserInEvent(eventId, participantId);

        return participantCompositeRepository.getAllParticipantsByEventId(eventId).stream()
                .map(participantMapper::toParticipant)
                .toList();
    }

    public UUID addParticipantToEvent(
            UUID eventId, UUID participantId, ParticipantCreate participantCreate) {

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsNotFinalized(eventId);
        participantsServiceValidator.checkUserInEvent(eventId, participantId);
        participantsServiceValidator.checkAdminOrOwnerRole(eventId, participantId);

        var participantLogin = participantCreate.getLogin();
        userService.validateLogin(participantLogin);
        var userToAdd = userService.getByLogin(participantLogin);

        participantsServiceValidator.checkIsAlreadyParticipant(eventId, userToAdd.getId());

        var participant =
                new ParticipantDTO(
                        userToAdd.getId(),
                        userToAdd.getLogin(),
                        userToAdd.getName(),
                        roleProperties.getParticipant(),
                        BigDecimal.valueOf(0));

        participantCompositeRepository.addParticipantToEvent(eventId, participant);
        return userToAdd.getId();
    }

    public UUID addParticipantToEvent(UUID eventId, UserEntity userEntity) {
        var participant =
                new ParticipantDTO(
                        userEntity.getId(),
                        userEntity.getLogin(),
                        userEntity.getName(),
                        roleProperties.getParticipant(),
                        BigDecimal.valueOf(0));

        participantCompositeRepository.addParticipantToEvent(eventId, participant);
        return userEntity.getId();
    }

    public void deleteParticipant(UUID eventId, UUID participantId, UUID userId) {

        participantsServiceValidator.checkIsSamePerson(userId, participantId);
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsNotFinalized(eventId);
        participantsServiceValidator.checkUserInEvent(eventId, userId);
        participantsServiceValidator.checkAdminOrOwnerRole(eventId, userId);
        participantsServiceValidator.checkUserInEvent(eventId, participantId);

        participantCompositeRepository.deleteParticipant(eventId, participantId);
    }

    public void updateParticipantRole(
            UUID eventId, UUID userId, UUID participantId, ParticipantSetRole participantSetRole) {

        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        eventServiceValidator.checkIsNotFinalized(eventId);
        participantsServiceValidator.checkUserInEvent(eventId, userId);
        participantsServiceValidator.checkOwnerRole(eventId, userId);
        participantsServiceValidator.checkUserInEvent(eventId, participantId);
        participantsServiceValidator.checkReplaceRole(eventId, participantId);

        var newRole = participantSetRole.getRole().getValue();
        EventRole eventRole = EventRole.valueOf(newRole);
        participantCompositeRepository.updateParticipantRole(eventId, participantId, eventRole);
    }

    public String getParticipantRoleInEvent(UUID eventId, UUID participantId) {
        return participantCompositeRepository.getParticipantRoleInEvent(eventId, participantId);
    }

    public boolean isOwnerRole(UUID eventId, UUID participantId) {
        return getParticipantRoleInEvent(eventId, participantId).equals(roleProperties.getOwner());
    }

    public boolean isAdminRole(UUID eventId, UUID participantId) {
        return getParticipantRoleInEvent(eventId, participantId).equals(roleProperties.getAdmin());
    }

    public boolean isParticipantRole(UUID eventId, UUID participantId) {
        return getParticipantRoleInEvent(eventId, participantId)
                .equals(roleProperties.getParticipant());
    }

    public UserBalance getParticipantBalance(UUID eventId, UUID participantId) {
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        participantsServiceValidator.checkUserInEvent(eventId, participantId);

        return participantBalanceMapper.toUserBalance(
                participantCompositeRepository.getParticipantBalance(eventId, participantId));
    }

    public List<ParticipantBalanceSummary> getParticipantsSummaryBalance(
            UUID eventId, UUID participantId) {
        eventServiceValidator.checkIsExistedAndNotDeleted(eventId);
        participantsServiceValidator.checkUserInEvent(eventId, participantId);
        participantsServiceValidator.checkAdminOrOwnerRole(eventId, participantId);

        return participantCompositeRepository.getSummaryParticipantBalance(eventId).stream()
                .map(participantSummaryBalanceMapper::toParticipantBalanceSummary)
                .toList();
    }
}
