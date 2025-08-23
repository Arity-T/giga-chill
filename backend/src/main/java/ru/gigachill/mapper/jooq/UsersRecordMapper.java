package ru.gigachill.mapper.jooq;

import com.github.giga_chill.jooq.generated.tables.records.UsersRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.gigachill.dto.UserDTO;
import ru.gigachill.model.DebtWithUserData;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UsersRecordMapper {

	@Mapping(source = "userId", target = "id")
	UserDTO toUserDTO(UsersRecord record);

	@Mapping(source = "userId", target = "id")
	UserDTO toUserDTO(DebtWithUserData debtData);
}
