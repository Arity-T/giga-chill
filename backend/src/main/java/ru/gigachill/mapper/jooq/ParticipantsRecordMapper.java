package ru.gigachill.mapper.jooq;

import com.github.giga_chill.jooq.generated.enums.EventRole;
import com.github.giga_chill.jooq.generated.tables.records.UserInEventRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.gigachill.data.transfer.object.ParticipantDTO;
import java.util.UUID;

@Mapper(componentModel = "spring", uses = {UsersRecordMapper.class})
public interface ParticipantsRecordMapper {

	@Mapping(source = "userId", target = "id")
	@Mapping(source = "role.literal", target = "role")
	ParticipantDTO toParticipantDTO(UserInEventRecord record);

	@Mapping(source = "id", target = "userId")
	@Mapping(source = "role", target = "role", qualifiedByName = "stringToEventRole")
	UserInEventRecord toUserInEventRecord(ParticipantDTO dto, UUID eventId);

	@Named("stringToEventRole")
	default EventRole stringToEventRole(String role) {
		return role == null ? null : EventRole.valueOf(role);
	}
}

