package ru.gigachill.mapper.jooq;

import com.github.giga_chill.jooq.generated.tables.records.UserInEventRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gigachill.data.transfer.object.ParticipantDTO;

@Mapper(componentModel = "spring", uses = {UsersRecordMapper.class})
public interface ParticipantsRecordMapper {

	@Mapping(source = "userId", target = "id")
	@Mapping(source = "role.literal", target = "role")
	ParticipantDTO toParticipantDTO(UserInEventRecord record);
}

