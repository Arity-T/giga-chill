package ru.gigachill.mapper.jooq;

import com.github.giga_chill.jooq.generated.tables.records.UsersRecord;
import com.github.giga_chill.jooq.generated.tables.records.UserInEventRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.gigachill.dto.ParticipantDTO;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UsersRecordMapper.class, ParticipantsRecordMapper.class})
public interface ShoppingListConsumerMapper {

	/**
	 * Maps a user to a basic participant DTO with login and name
	 */
	@Mapping(source = "userId", target = "id")
	ParticipantDTO toParticipantDTOFromUser(UsersRecord userRecord);

	/**
	 * Maps a user in event record to participant DTO with role
	 */
  	@Mapping(source = "userId", target = "id")
	ParticipantDTO toParticipantDTOFromUserInEvent(UserInEventRecord userInEventRecord);

	/**
	 * Creates a fallback participant DTO when user data is missing
	 */
	default ParticipantDTO createFallbackParticipant(UUID userId) {
		return new ParticipantDTO(userId, null, null, null, null);
	}
} 