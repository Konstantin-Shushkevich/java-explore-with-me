package ru.practicum.service.event.service.nonpublic;

import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.event.dto.NewEventDto;
import ru.practicum.service.event.dto.UpdateEventUserRequest;
import ru.practicum.service.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.service.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.service.request.dto.ParticipationRequestDto;
import ru.practicum.service.util.pageable.PageRequestParams;

import java.util.List;

public interface EventServicePrivate {
    List<EventShortDto> getByAuthor(Long userId, PageRequestParams pageRequestParams);

    EventFullDto add(Long userId, NewEventDto newEventDto);

    EventFullDto findById(Long userId, Long eventId);

    EventFullDto updateById(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(Long userId,
                                                       Long eventId,
                                                       EventRequestStatusUpdateRequest request);
}
