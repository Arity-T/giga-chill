package ru.gigachill.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gigachill.dto.UserDTO;
import ru.gigachill.web.api.model.Debt;

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
                .peek(item -> item.setAmount(item.getAmount().setScale(2, RoundingMode.HALF_UP)))
                .collect(Collectors.toList());
    }
}
