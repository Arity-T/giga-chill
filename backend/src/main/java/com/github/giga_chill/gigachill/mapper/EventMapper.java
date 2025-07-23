package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.EventDTO;
import com.github.giga_chill.gigachill.web.info.ResponseEventInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UuidMapper.class)
public interface EventMapper {

    @Mapping(source = "eventId", target = "eventId", qualifiedByName = "stringToUuid")
    EventDTO toDto(ResponseEventInfo entity);

    @Mapping(source = "eventId", target = "eventId", qualifiedByName = "uuidToString")
    ResponseEventInfo toInfo(EventDTO dto);
}
