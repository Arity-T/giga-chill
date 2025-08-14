package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.UserDTO;
import com.github.giga_chill.gigachill.model.UserEntity;
import com.github.giga_chill.gigachill.web.api.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toUserDto(UserEntity entity);

    UserEntity toUserEntity(UserDTO dto);

    User toUser(UserEntity user);

    User toUser(UserDTO user);
}
