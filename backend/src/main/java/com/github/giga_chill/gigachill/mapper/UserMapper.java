package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.UserDTO;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.web.info.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UuidMapper.class)
public interface UserMapper {

    @Mapping(source = "id", target = "id", qualifiedByName = "stringToUuid")
    UserDTO toDto(UserInfo entity);

    @Mapping(source = "id", target = "id", qualifiedByName = "stringToUuid")
    UserDTO toDto(User entity);

    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToString")
    UserInfo toInfo(UserDTO dto);

    @Mapping(source = "id", target = "id", qualifiedByName = "stringToUuid")
    User toEntity(UserDTO dto);
}
