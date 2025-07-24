package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.UserDTO;
import com.github.giga_chill.gigachill.model.User;
import com.github.giga_chill.gigachill.web.info.UserInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UuidMapper.class)
public interface UserMapper {

    @Mapping(source = "id", target = "id", qualifiedByName = "stringToUuid")
    UserDTO toUserDto(UserInfo info);

    UserDTO toUserDto(User entity);

    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToString")
    UserInfo toUserInfo(UserDTO dto);

    User toUserEntity(UserDTO dto);
}
