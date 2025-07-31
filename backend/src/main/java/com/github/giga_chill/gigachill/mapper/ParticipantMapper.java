package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.ParticipantDTO;
import com.github.giga_chill.gigachill.web.info.ConsumerInfo;
import com.github.giga_chill.gigachill.web.info.ParticipantInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UuidMapper.class)
public interface ParticipantMapper {

    @Mapping(source = "role", target = "userRole")
    ParticipantInfo toParticipantInfo(ParticipantDTO dto);

    @Mapping(source = "role", target = "userRole")
    ConsumerInfo toConsumerInfo(ParticipantDTO dto);
}
