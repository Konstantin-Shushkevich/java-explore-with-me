package ru.practicum.service.request.service;

import ru.practicum.service.request.dto.ParticipationRequestDto;

import java.util.List;

public interface ParticipationRequestService {
    List<ParticipationRequestDto> findAllForUserByFilters(Long userId);

    ParticipationRequestDto add(Long userId, Long eventId);

    ParticipationRequestDto updateStatus(Long userId, Long requestId);
}
