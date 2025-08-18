package ru.gigachill.mapper;

import org.mapstruct.Mapper;
import ru.gigachill.data.transfer.object.EventDTO;
import ru.gigachill.web.api.model.Event;

@Mapper(componentModel = "spring")
public interface EventMapper {
    Event toEvent(EventDTO dto);
}
