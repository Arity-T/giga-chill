package ru.gigachill.mapper.jooq;

import com.github.giga_chill.jooq.generated.enums.EventRole;
import com.github.giga_chill.jooq.generated.tables.records.UserInEventRecord;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import ru.gigachill.dto.ParticipantDTO;
import ru.gigachill.model.ConsumerWithUserData;
import ru.gigachill.model.UserInEventWithUserData;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {UsersRecordMapper.class})
public interface ParticipantsRecordMapper {

    @Mapping(source = "userId", target = "id")
    @Mapping(source = "role.literal", target = "role")
    // login и name выставляются отдельно, так как это другой UsersRecord
    ParticipantDTO toParticipantDTO(UserInEventRecord record);

    @Mapping(source = "userId", target = "id")
    @Mapping(source = "role.literal", target = "role")
    ParticipantDTO toParticipantDTO(UserInEventWithUserData record);

    @Mapping(source = "userId", target = "id")
    @Mapping(source = "role.literal", target = "role")
    ParticipantDTO toParticipantDTO(ConsumerWithUserData consumerData);

    @Mapping(source = "dto.id", target = "userId")
    @Mapping(source = "dto.role", target = "role", qualifiedByName = "stringToEventRole")
    @Mapping(source = "eventId", target = "eventId")
    UserInEventRecord toUserInEventRecord(ParticipantDTO dto, UUID eventId);

    @Named("stringToEventRole")
    default EventRole stringToEventRole(String role) {
        return role == null ? null : EventRole.valueOf(role);
    }
}
