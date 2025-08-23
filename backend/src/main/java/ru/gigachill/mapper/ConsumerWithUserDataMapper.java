package ru.gigachill.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.gigachill.dto.ParticipantDTO;
import ru.gigachill.model.ConsumerWithUserData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConsumerWithUserDataMapper {

    @Mapping(source = "userId", target = "id")
    @Mapping(source = "role.literal", target = "role")
    ParticipantDTO toParticipantDTO(ConsumerWithUserData consumerData);
}
