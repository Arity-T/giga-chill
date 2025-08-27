package ru.gigachill.mapper;

import org.mapstruct.Mapper;
import ru.gigachill.dto.UserDTO;
import ru.gigachill.model.UserEntity;
import ru.gigachill.web.api.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toUserDto(UserEntity entity);

    UserEntity toUserEntity(UserDTO dto);

    User toUser(UserEntity user);

    User toUser(UserDTO user);
}
