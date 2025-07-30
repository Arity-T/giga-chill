package com.github.giga_chill.gigachill.mapper;

import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UuidMapper {
    @Named("uuidToString")
    default String uuidToString(UUID id) {
        return id == null ? null : id.toString();
    }

    @Named("stringToUuid")
    default UUID stringToUuid(String str) {
        return str == null ? null : UUID.fromString(str);
    }
}
