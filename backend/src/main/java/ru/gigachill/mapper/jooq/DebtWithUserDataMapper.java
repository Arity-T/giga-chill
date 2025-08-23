package ru.gigachill.mapper.jooq;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.gigachill.dto.UserDTO;
import ru.gigachill.model.DebtWithUserData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DebtWithUserDataMapper {

    @Mapping(source = "userId", target = "id")
    UserDTO toUserDTO(DebtWithUserData debtData);
}
