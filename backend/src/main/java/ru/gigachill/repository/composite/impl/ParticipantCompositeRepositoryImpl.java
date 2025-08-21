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

    // todo: optimize in the repository
    @Override
    public boolean checkUserInEvent(UUID eventId, UUID userId) {
        List<UserInEventRecord> records = userInEventRepository.findByEventId(eventId);
        for (UserInEventRecord record : records) {
            if (record.getUserId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    // todo: optimize in the repository
    @Transactional
    @Override
    public void updateParticipantRole(UUID eventId, UUID participantId, String role) {
        List<UserInEventRecord> records = userInEventRepository.findByEventId(eventId);
        for (UserInEventRecord record : records) {
            if (record.getUserId().equals(participantId)) {
                record.setRole(EventRole.valueOf(role));
                record.update();
                break;
            }
        }
    }

    // todo: optimize in the repository
    @Override
    public String getParticipantRoleInEvent(UUID eventId, UUID participantId) {
        List<UserInEventRecord> records = userInEventRepository.findByEventId(eventId);
        for (UserInEventRecord record : records) {
            if (record.getUserId().equals(participantId)) {
                return record.getRole() != null ? record.getRole().getLiteral() : null;
            }
        }
        return null;
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
                eventRepository.findDebtsICreated(eventId, participantId).stream()
                        // Фильтруем долги самому себе
                        .filter(entry -> !entry.getKey().equals(participantId))
                        .map(
                                entry -> {
                                    var userOpt = userRepository.findById(entry.getKey());
                                    return userOpt.map(user -> Map.of(usersRecordMapper.toUserDTO(user), entry.getValue()))
                                            .orElse(null);
                                })
                        .filter(Objects::nonNull)
                        .toList();

        var debtsToMe =
                eventRepository.findDebtsToMe(eventId, participantId).stream()
                        // Фильтруем долги самому себе
                        .filter(entry -> !entry.getKey().equals(participantId))
                        .map(
                                entry -> {
                                    var userOpt = userRepository.findById(entry.getKey());
                                    return userOpt.map(user -> Map.of(usersRecordMapper.toUserDTO(user), entry.getValue()))
                                            .orElse(null);
                                })
                        .filter(Objects::nonNull)
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
