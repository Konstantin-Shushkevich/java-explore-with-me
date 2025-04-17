package ru.practicum.service.event.service.publics;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.EventShortDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EventServicePublic {
    List<EventShortDto> findByFilters(String text,
                                      List<Long> categories,
                                      Boolean paid,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      Boolean onlyAvailable,
                                      String sort,
                                      int from,
                                      int size,
                                      HttpServletRequest request);

    EventFullDto findById(Long id, HttpServletRequest request);
}
