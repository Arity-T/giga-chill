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

import org.springframework.stereotype.Service;

import com.github.giga_chill.gigachill.repository.UserRepository;
import com.github.giga_chill.jooq.generated.tables.records.UsersRecord;

@Service
public class ParticipantDAOImpl implements ParticipantDAO {

  private final UserRepository userRepository;
  private final UserInEventRepository userInEventRepository;

  public ParticipantDAOImpl(UserRepository userRepository, UserInEventRepository userInEventRepository) {
    this.userRepository = userRepository;
    this.userInEventRepository = userInEventRepository;
  }

  @Override
  public List<ParticipantDTO> getAllParticipantsByEventId(String eventId) {
    List<UserInEventRecord> records = userInEventRepository.findByEventId(UUID.fromString(eventId));
    List<ParticipantDTO> participants = new ArrayList<>();
    for (UserInEventRecord record : records) {
      UsersRecord userRecord = userRepository.findById(record.getUserId()).orElse(null);
      participants.add(new ParticipantDTO(
        record.getUserId().toString(),
        userRecord.getLogin(),
        userRecord.getName(),
        record.getRole() != null ? record.getRole().getLiteral() : null,
        record.getBalance()
      ));
    }
    return participants;
  }

  @Override
  public void addParticipantToEvent(String eventId, ParticipantDTO participant) {
    UserInEventRecord record = new UserInEventRecord();
    record.setUserId(UUID.fromString(participant.id()));
    record.setEventId(UUID.fromString(eventId));
    record.setRole(participant.role() != null ? EventRole.valueOf(participant.role()) : null);
    userInEventRepository.save(record);
  }

  @Override
  public void deleteParticipant(String eventId, String participantId) {
    userInEventRepository.deleteById(UUID.fromString(eventId), UUID.fromString(participantId));
  }

  @Override
  public boolean isParticipant(String eventId, String userId) {
    List<UserInEventRecord> records = userInEventRepository.findByEventId(UUID.fromString(eventId));
    for (UserInEventRecord record : records) {
      if (record.getUserId().toString().equals(userId)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void updateParticipantRole(String eventId, String participantId, String role) {
    List<UserInEventRecord> records = userInEventRepository.findByEventId(UUID.fromString(eventId));
    for (UserInEventRecord record : records) {
      if (record.getUserId().toString().equals(participantId)) {
        record.setRole(EventRole.valueOf(role));
        record.update();
        break;
      }
    }
  }

  @Override
  public String getParticipantRoleInEvent(String eventId, String participantId) {
    List<UserInEventRecord> records = userInEventRepository.findByEventId(UUID.fromString(eventId));
    for (UserInEventRecord record : records) {
      if (record.getUserId().toString().equals(participantId)) {
        return record.getRole() != null ? record.getRole().getLiteral() : null;
      }
    }
    return null;
  }

  @Override
  public ParticipantDTO getParticipantById(String eventId, String participantId) {
    // Получаем Optional<UserInEventRecord>
    Optional<UserInEventRecord> userInEventOpt = userInEventRepository.findById(
        UUID.fromString(eventId), UUID.fromString(participantId)
    );
    if (userInEventOpt.isEmpty()) {
        return null;
    }
    UserInEventRecord userInEvent = userInEventOpt.get();

    Optional<UsersRecord> userRecordOpt = userRepository.findById(UUID.fromString(participantId));
    if (userRecordOpt.isEmpty()) {
        return null;
    }
    UsersRecord userRecord = userRecordOpt.get();

    EventRole userRole = userInEvent.getRole();

    return new ParticipantDTO(
        userRecord.getUserId().toString(),
        userRecord.getLogin(),
        userRecord.getName(),
        userRole != null ? userRole.getLiteral() : null,
        userInEvent.getBalance()
    );
  }
}