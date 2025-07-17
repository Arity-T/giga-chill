package com.github.giga_chill.gigachill.data.access.object.impl;

import com.github.giga_chill.gigachill.data.access.object.ParticipantDAO;
import com.github.giga_chill.gigachill.data.transfer.object.ParticipantBalanceDTO;
import com.github.giga_chill.gigachill.data.transfer.object.ParticipantDTO;
import com.github.giga_chill.gigachill.data.transfer.object.UserDTO;
import com.github.giga_chill.gigachill.repository.EventRepository;
import com.github.giga_chill.gigachill.repository.UserInEventRepository;
import com.github.giga_chill.gigachill.repository.UserRepository;
import com.github.giga_chill.jooq.generated.enums.EventRole;
import com.github.giga_chill.jooq.generated.tables.records.UserInEventRecord;
import com.github.giga_chill.jooq.generated.tables.records.UsersRecord;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParticipantDAOImpl implements ParticipantDAO {
    private final UserRepository userRepository;
    private final UserInEventRepository userInEventRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipantDTO> getAllParticipantsByEventId(UUID eventId) {
        List<UserInEventRecord> records = userInEventRepository.findByEventId(eventId);
        List<ParticipantDTO> participants = new ArrayList<>();
        for (UserInEventRecord record : records) {
            UsersRecord userRecord = userRepository.findById(record.getUserId()).orElse(null);
            participants.add(
                    new ParticipantDTO(
                            record.getUserId(),
                            userRecord.getLogin(),
                            userRecord.getName(),
                            record.getRole() != null ? record.getRole().getLiteral() : null,
                            record.getBalance()));
        }
        return participants;
    }

    @Override
    public void addParticipantToEvent(UUID eventId, ParticipantDTO participant) {
        UserInEventRecord record = new UserInEventRecord();
        record.setUserId(participant.id());
        record.setEventId(eventId);
        record.setRole(participant.role() != null ? EventRole.valueOf(participant.role()) : null);
        userInEventRepository.save(record);
    }

    @Override
    public void deleteParticipant(UUID eventId, UUID participantId) {
        userInEventRepository.deleteById(eventId, participantId);
    }

    // todo: optimize in the repository
    @Override
    public boolean isParticipant(UUID eventId, UUID userId) {
        List<UserInEventRecord> records = userInEventRepository.findByEventId(eventId);
        for (UserInEventRecord record : records) {
            if (record.getUserId().equals(userId)) {
                return true;
            }
        }
        return false;
    }

    // todo: optimize in the repository
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

        EventRole userRole = userInEvent.getRole();

        return new ParticipantDTO(
                userRecord.getUserId(),
                userRecord.getLogin(),
                userRecord.getName(),
                userRole != null ? userRole.getLiteral() : null,
                userInEvent.getBalance());
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
                        .map(
                                entry -> {
                                    var userOpt = userRepository.findById(entry.getKey());
                                    return userOpt.map(
                                                    user ->
                                                            Map.of(
                                                                    new UserDTO(
                                                                            user.getUserId(),
                                                                            user.getLogin(),
                                                                            user.getName()),
                                                                    entry.getValue()))
                                            .orElse(null);
                                })
                        .filter(Objects::nonNull)
                        .toList();

        var debtsToMe =
                eventRepository.findDebtsToMe(eventId, participantId).stream()
                        .map(
                                entry -> {
                                    var userOpt = userRepository.findById(entry.getKey());
                                    return userOpt.map(
                                                    user ->
                                                            Map.of(
                                                                    new UserDTO(
                                                                            user.getUserId(),
                                                                            user.getLogin(),
                                                                            user.getName()),
                                                                    entry.getValue()))
                                            .orElse(null);
                                })
                        .filter(Objects::nonNull)
                        .toList();

        return new ParticipantBalanceDTO(myDebts, debtsToMe);
    }
}
