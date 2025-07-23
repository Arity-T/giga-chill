package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.UserDTO;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.util.UuidUtils;
import com.github.giga_chill.gigachill.web.info.UserInfo;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "id", target = "id", qualifiedByName = "stringToUuid")
    UserDTO toDto(UserInfo entity);

    @Mapping(source = "id", target = "id", qualifiedByName = "stringToUuid")
    UserDTO toDto(User entity);

    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToString")
    UserInfo toInfo(UserDTO dto);

    @Mapping(source = "id", target = "id", qualifiedByName = "stringToUuid")
    User toEntity(UserDTO dto);

    @Named("uuidToString")
    default String uuidToString(UUID eventId) {
        return eventId == null ? null : eventId.toString();
    }

    @Named("stringToUuid")
    default UUID stringToUuid(String eventId) {
        return eventId == null ? null : UuidUtils.safeUUID(eventId);
    }
}
