package ru.gigachill.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.gigachill.dto.EventDTO;
import ru.gigachill.web.api.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "userRole", ignore = true)
    Event toEvent(EventDTO dto);
}
