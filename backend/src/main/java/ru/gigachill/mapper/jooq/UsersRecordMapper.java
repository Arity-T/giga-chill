package ru.gigachill.mapper.jooq;

import com.github.giga_chill.jooq.generated.tables.records.UsersRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gigachill.data.transfer.object.UserDTO;

@Mapper(componentModel = "spring")
public interface UsersRecordMapper {

	@Mapping(source = "userId", target = "id")
	UserDTO toUserDTO(UsersRecord record);
}

