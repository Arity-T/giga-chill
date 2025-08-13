package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.UserDTO;
import com.github.giga_chill.gigachill.web.api.model.Debt;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface DebtMapper {

    @Mapping(source = "key", target = "user")
    @Mapping(source = "value", target = "amount")
    Debt toDebt(Map.Entry<UserDTO, BigDecimal> entry);

    default List<Debt> toDebtInfoList(List<Map<UserDTO, BigDecimal>> maps) {
        if (Objects.isNull(maps)) {
            return null;
        }
        return maps.stream()
                .filter(Objects::nonNull)
                .flatMap(map -> map.entrySet().stream())
                .map(this::toDebt)
                .collect(Collectors.toList());
    }
}
