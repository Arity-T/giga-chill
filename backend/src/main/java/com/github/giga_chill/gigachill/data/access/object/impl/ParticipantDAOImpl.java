package com.github.giga_chill.gigachill.data.access.object.impl;

import com.github.giga_chill.gigachill.data.access.object.ParticipantDAO;
import com.github.giga_chill.gigachill.repository.UserInEventRepository;
import com.github.giga_chill.jooq.generated.tables.records.UserInEventRecord;
import com.github.giga_chill.gigachill.data.transfer.object.ParticipantDTO;
import com.github.giga_chill.jooq.generated.enums.EventRole;

import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.github.giga_chill.gigachill.repository.UserRepository;
import com.github.giga_chill.jooq.generated.tables.records.UsersRecord;

@Service
@RequiredArgsConstructor
public class ParticipantDAOImpl implements ParticipantDAO {
  private final UserRepository userRepository;
  private final UserInEventRepository userInEventRepository;

  @Override
  public List<ParticipantDTO> getAllParticipantsByEventId(UUID eventId) {
    List<UserInEventRecord> records = userInEventRepository.findByEventId(eventId);
    List<ParticipantDTO> participants = new ArrayList<>();
    for (UserInEventRecord record : records) {
      UsersRecord userRecord = userRepository.findById(record.getUserId()).orElse(null);
      participants.add(new ParticipantDTO(
        record.getUserId(),
        userRecord.getLogin(),
        userRecord.getName(),
        record.getRole() != null ? record.getRole().getLiteral() : null,
        record.getBalance()
      ));
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
    Optional<UserInEventRecord> userInEventOpt = userInEventRepository.findById(
        eventId, participantId
    );
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
        userInEvent.getBalance()
    );
  }
}