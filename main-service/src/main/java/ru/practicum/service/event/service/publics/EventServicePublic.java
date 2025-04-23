package ru.practicum.service.event.service.publics;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.event.dto.params.EventPublicFilterParams;

import java.util.List;

public interface EventServicePublic {
    List<EventShortDto> findByFilters(EventPublicFilterParams eventPublicFilterParams, HttpServletRequest request);

    EventFullDto findById(Long id, HttpServletRequest request);
}
