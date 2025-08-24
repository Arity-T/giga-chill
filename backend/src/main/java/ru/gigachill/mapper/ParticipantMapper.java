package ru.gigachill.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.gigachill.dto.ParticipantDTO;
import ru.gigachill.web.api.model.Participant;
import ru.gigachill.web.api.model.UserRole;

@Mapper(componentModel = "spring")
public interface ParticipantMapper {

    @Mapping(source = "role", target = "userRole", qualifiedByName = "stringToUserRole")
    Participant toParticipant(ParticipantDTO dto);

    @Named("stringToUserRole")
    default UserRole stringToUserRole(String role) {
        return UserRole.fromValue(role);
    }
}
