package ru.gigachill.repository.composite.impl;

import com.github.giga_chill.jooq.generated.enums.EventRole;
import com.github.giga_chill.jooq.generated.tables.records.UserInEventRecord;
import com.github.giga_chill.jooq.generated.tables.records.UsersRecord;
import java.math.BigDecimal;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.gigachill.repository.composite.ParticipantCompositeRepository;
import ru.gigachill.dto.ParticipantBalanceDTO;
import ru.gigachill.dto.ParticipantDTO;
import ru.gigachill.dto.ParticipantSummaryBalanceDTO;
import ru.gigachill.dto.UserDTO;
import ru.gigachill.repository.simple.EventRepository;
import ru.gigachill.repository.simple.UserInEventRepository;
import ru.gigachill.repository.simple.UserRepository;
import ru.gigachill.mapper.jooq.ParticipantsRecordMapper;
import ru.gigachill.mapper.jooq.UsersRecordMapper;
import ru.gigachill.mapper.jooq.DebtWithUserDataMapper;

@Transactional(readOnly = true)
@Repository
@RequiredArgsConstructor
public class ParticipantCompositeRepositoryImpl implements ParticipantCompositeRepository {
    private final UserRepository userRepository;
    private final UserInEventRepository userInEventRepository;
    private final EventRepository eventRepository;
    private final ParticipantsRecordMapper participantsRecordMapper;
    private final UsersRecordMapper usersRecordMapper;
    private final DebtWithUserDataMapper debtWithUserDataMapper;

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
        // Получаем Optional<UserInEventRecord>
        Optional<UserInEventRecord> userInEventOpt =
                userInEventRepository.findById(eventId, participantId);
        if (userInEventOpt.isEmpty()) {
            return null;
        }
        UserInEventRecord userInEvent = userInEventOpt.get();

        Optional<UsersRecord> userRecordOpt = userRepository.findById(participantId);
        if (userRecordOpt.isEmpty()) {
            return null;
        }
        UsersRecord userRecord = userRecordOpt.get();

        ParticipantDTO dto = participantsRecordMapper.toParticipantDTO(userInEvent);
        dto.setLogin(userRecord.getLogin());
        dto.setName(userRecord.getName());
        return dto;
    }

    /**
     * Retrieves the current balance summary for the specified participant.
     *
     * @param eventId the unique identifier of the event
     * @param participantId the unique identifier of the participant
     * @return a {@link ParticipantBalanceDTO} containing the participant’s total debits, credits,
     *     and net balance; never {@code null}
     */
    @Override
    public ParticipantBalanceDTO getParticipantBalance(UUID eventId, UUID participantId) {
        var myDebts =
                eventRepository.findDebtsICreatedWithUserData(eventId, participantId).stream()
                        // Фильтруем долги самому себе и null userId
                        .filter(debt -> debt.getUserId() != null && !debt.getUserId().equals(participantId))
                        .map(debt -> Map.of(debtWithUserDataMapper.toUserDTO(debt), debt.getAmount()))
                        .toList();

        var debtsToMe =
                eventRepository.findDebtsToMeWithUserData(eventId, participantId).stream()
                        // Фильтруем долги самому себе и null userId
                        .filter(debt -> debt.getUserId() != null && !debt.getUserId().equals(participantId))
                        .map(debt -> Map.of(debtWithUserDataMapper.toUserDTO(debt), debt.getAmount()))
                        .toList();

        return new ParticipantBalanceDTO(myDebts, debtsToMe);
    }

    /**
     * Computes and retrieves a summary of balance information for each participant in the given
     * event.
     *
     * @param eventId the unique identifier of the event for which to calculate participant balances
     * @return a {@link List} of {@link ParticipantSummaryBalanceDTO} objects
     */
    @Override
    public List<ParticipantSummaryBalanceDTO> getSummaryParticipantBalance(UUID eventId) {
        // Получаем всех участников мероприятия
        List<UserInEventRecord> participants = userInEventRepository.findByEventId(eventId);

        List<ParticipantSummaryBalanceDTO> result = new ArrayList<>();

        for (UserInEventRecord participant : participants) {
            UUID userId = participant.getUserId();

            // Считаем, сколько этот участник должен другим (по debtor_id)
            BigDecimal totalDebts =
                    eventRepository.findDebtsICreated(eventId, userId).stream()
                            .map(Map.Entry::getValue)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Считаем, сколько должны этому участнику (по creditor_id)
            BigDecimal totalCredits =
                    eventRepository.findDebtsToMe(eventId, userId).stream()
                            .map(Map.Entry::getValue)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Итоговый баланс: мне должны - я должен
            BigDecimal totalBalance = totalCredits.subtract(totalDebts);

            // Подробный баланс (детализация по каждому пользователю)
            ParticipantBalanceDTO userBalance = getParticipantBalance(eventId, userId);

            // Получаем UserDTO
            UsersRecord userRecord = userRepository.findById(userId).orElse(null);
            UserDTO userDTO = userRecord == null ? null : usersRecordMapper.toUserDTO(userRecord);

            result.add(new ParticipantSummaryBalanceDTO(userDTO, totalBalance, userBalance));
        }

        return result;
    }
}
