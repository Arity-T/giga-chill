package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.EventDTO;
import com.github.giga_chill.gigachill.util.UuidUtils;
import com.github.giga_chill.gigachill.web.info.ResponseEventInfo;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(source = "eventId", target = "eventId", qualifiedByName = "stringToUuid")
    EventDTO toDto(ResponseEventInfo entity);

    @Mapping(source = "eventId", target = "eventId", qualifiedByName = "uuidToString")
    ResponseEventInfo toInfo(EventDTO dto);

    @Named("uuidToString")
    default String uuidToString(UUID eventId) {
        return eventId == null ? null : eventId.toString();
    }

    @Named("stringToUuid")
    default UUID stringToUuid(String eventId) {
        return eventId == null ? null : UuidUtils.safeUUID(eventId);
    }
}
