package ru.practicum.service.event.service.admin;

import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.UpdateEventAdminRequest;
import ru.practicum.service.event.dto.params.EventAdminFilterParams;

import java.util.List;

public interface EventServiceAdmin {
    List<EventFullDto> findAllByCriteria(EventAdminFilterParams eventAdminFilterParams);

    EventFullDto updateById(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
