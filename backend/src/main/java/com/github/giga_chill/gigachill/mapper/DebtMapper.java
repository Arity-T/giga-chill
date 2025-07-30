package com.github.giga_chill.gigachill.mapper;

import com.github.giga_chill.gigachill.data.transfer.object.UserDTO;
import com.github.giga_chill.gigachill.web.info.DebtInfo;
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
    DebtInfo toDebtInfo(Map.Entry<UserDTO, BigDecimal> entry);

    default List<DebtInfo> toDebtInfoList(List<Map<UserDTO, BigDecimal>> maps) {
        if (maps == null) {
            return null;
        }
        return maps.stream()
                .filter(Objects::nonNull)
                .flatMap(map -> map.entrySet().stream())
                .map(this::toDebtInfo)
                .collect(Collectors.toList());
    }
}
