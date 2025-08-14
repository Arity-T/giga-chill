package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.ParticipantDTO;
import com.github.giga_chill.gigachill.web.api.model.Participant;
import com.github.giga_chill.gigachill.web.api.model.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ParticipantMapper {

    @Mapping(source = "role", target = "userRole", qualifiedByName = "stringToUserRole")
    Participant toParticipant(ParticipantDTO dto);

    @Named("stringToUserRole")
    default UserRole stringToUserRole(String role) {
        return UserRole.fromValue(role);
    }
}
