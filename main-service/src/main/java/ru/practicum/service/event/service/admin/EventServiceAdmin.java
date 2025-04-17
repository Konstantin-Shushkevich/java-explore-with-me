package ru.practicum.service.event.service.admin;

import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.UpdateEventAdminRequest;
import ru.practicum.service.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventServiceAdmin {
    List<EventFullDto> findAllByCriteria(List<Long> users,
                                         List<EventState> states,
                                         List<Long> categories,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         int from,
                                         int size);

    EventFullDto updateById(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);
}
