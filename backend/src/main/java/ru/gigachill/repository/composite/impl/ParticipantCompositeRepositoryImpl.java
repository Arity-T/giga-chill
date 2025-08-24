package ru.gigachill.repository.composite.impl;

import com.github.giga_chill.jooq.generated.enums.EventRole;
import com.github.giga_chill.jooq.generated.tables.records.UserInEventRecord;
import com.github.giga_chill.jooq.generated.tables.records.UsersRecord;
import java.math.BigDecimal;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.gigachill.dto.ParticipantBalanceDTO;
import ru.gigachill.dto.ParticipantDTO;
import ru.gigachill.dto.ParticipantSummaryBalanceDTO;
import ru.gigachill.dto.UserDTO;
import ru.gigachill.mapper.jooq.ParticipantsRecordMapper;
import ru.gigachill.mapper.jooq.UsersRecordMapper;
import ru.gigachill.repository.composite.ParticipantCompositeRepository;
import ru.gigachill.repository.simple.EventRepository;
import ru.gigachill.repository.simple.UserInEventRepository;
import ru.gigachill.repository.simple.UserRepository;

@Transactional(readOnly = true)
@Repository
@RequiredArgsConstructor
public class ParticipantCompositeRepositoryImpl implements ParticipantCompositeRepository {
    private final UserRepository userRepository;
    private final UserInEventRepository userInEventRepository;
    private final EventRepository eventRepository;
    private final ParticipantsRecordMapper participantsRecordMapper;
    private final UsersRecordMapper usersRecordMapper;

    @Override
    public List<ParticipantDTO> getAllParticipantsByEventId(UUID eventId) {
        return userInEventRepository.findByEventIdWithUserData(eventId).stream()
                .map(participantsRecordMapper::toParticipantDTO)
                .toList();
    }

    @Transactional
    @Override
    public void addParticipantToEvent(UUID eventId, ParticipantDTO participant) {
        userInEventRepository.save(
                participantsRecordMapper.toUserInEventRecord(participant, eventId));
    }

    @Transactional
    @Override
    public void deleteParticipant(UUID eventId, UUID participantId) {
        userInEventRepository.deleteById(eventId, participantId);
    }

    @Override
    public boolean checkUserInEvent(UUID eventId, UUID userId) {
        return userInEventRepository.existsByEventIdAndUserId(eventId, userId);
    }

    @Transactional
    @Override
    public void updateParticipantRole(UUID eventId, UUID participantId, EventRole role) {
        userInEventRepository.updateRole(eventId, participantId, role);
    }

    @Override
    public String getParticipantRoleInEvent(UUID eventId, UUID participantId) {
        return userInEventRepository.getRole(eventId, participantId).toString();
    }

    @Override
    public ParticipantDTO getParticipantById(UUID eventId, UUID participantId) {
        // Get participant's role and event-specific data
        Optional<UserInEventRecord> userInEventOpt =
                userInEventRepository.findById(eventId, participantId);
        if (userInEventOpt.isEmpty()) {
            return null;
        }
        UserInEventRecord userInEvent = userInEventOpt.get();

        // Get user's general profile data
        Optional<UsersRecord> userRecordOpt = userRepository.findById(participantId);
        if (userRecordOpt.isEmpty()) {
            return null;
        }
        UsersRecord userRecord = userRecordOpt.get();

        // Combine event-specific and general user data
        ParticipantDTO dto = participantsRecordMapper.toParticipantDTO(userInEvent);
        dto.setLogin(userRecord.getLogin());
        dto.setName(userRecord.getName());
        return dto;
    }

    /**
     * Retrieves the current balance summary for the specified participant.
     *
     * <p>This method calculates the participant's debt relationships: - myDebts: amounts this
     * participant owes to others (debtor_id = participantId) - debtsToMe: amounts others owe to
     * this participant (creditor_id = participantId)
     *
     * <p>Self-debts and null user references are filtered out to ensure data integrity.
     */
    @Override
    public ParticipantBalanceDTO getParticipantBalance(UUID eventId, UUID participantId) {
        var myDebts =
                eventRepository.findDebtsICreatedWithUserData(eventId, participantId).stream()
                        // Filter out self-debts and null user references
                        .filter(
                                debt ->
                                        debt.getUserId() != null
                                                && !debt.getUserId().equals(participantId))
                        .map(debt -> Map.of(usersRecordMapper.toUserDTO(debt), debt.getAmount()))
                        .toList();

        var debtsToMe =
                eventRepository.findDebtsToMeWithUserData(eventId, participantId).stream()
                        // Filter out self-debts and null user references
                        .filter(
                                debt ->
                                        debt.getUserId() != null
                                                && !debt.getUserId().equals(participantId))
                        .map(debt -> Map.of(usersRecordMapper.toUserDTO(debt), debt.getAmount()))
                        .toList();

        return new ParticipantBalanceDTO(myDebts, debtsToMe);
    }

    /**
     * Computes and retrieves a summary of balance information for each participant in the given
     * event.
     *
     * <p>This method performs comprehensive balance calculation for all participants: 1. Loads all
     * participants in the event 2. For each participant, calculates total debts (amounts they owe)
     * and credits (amounts owed to them) 3. Computes net balance (credits - debts) 4. Retrieves
     * detailed balance breakdown and user profile data 5. Returns complete balance summary for each
     * participant
     */
    @Override
    public List<ParticipantSummaryBalanceDTO> getSummaryParticipantBalance(UUID eventId) {
        // Get all participants in the event
        List<UserInEventRecord> participants = userInEventRepository.findByEventId(eventId);

        List<ParticipantSummaryBalanceDTO> result = new ArrayList<>();

        for (UserInEventRecord participant : participants) {
            UUID userId = participant.getUserId();

            // Calculate total debts this participant owes to others (debtor_id = userId)
            BigDecimal totalDebts =
                    eventRepository.findDebtsICreated(eventId, userId).stream()
                            .map(Map.Entry::getValue)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Calculate total credits owed to this participant (creditor_id = userId)
            BigDecimal totalCredits =
                    eventRepository.findDebtsToMe(eventId, userId).stream()
                            .map(Map.Entry::getValue)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Net balance: credits owed to me - debts I owe
            BigDecimal totalBalance = totalCredits.subtract(totalDebts);

            // Get detailed balance breakdown per user
            ParticipantBalanceDTO userBalance = getParticipantBalance(eventId, userId);

            // Get user profile data
            UsersRecord userRecord = userRepository.findById(userId).orElse(null);
            UserDTO userDTO = userRecord == null ? null : usersRecordMapper.toUserDTO(userRecord);

            result.add(new ParticipantSummaryBalanceDTO(userDTO, totalBalance, userBalance));
        }

        return result;
    }
}
