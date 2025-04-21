package ru.practicum.service.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.event.dto.params.EventPublicFilterParams;
import ru.practicum.service.event.service.publics.EventServicePublic;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventPublicController {

    private final EventServicePublic eventServicePublic;

    @GetMapping
    public List<EventShortDto> findByFilters(@Valid @ModelAttribute EventPublicFilterParams eventPublicFilterParams,
                                             HttpServletRequest request) {
        log.trace("Getting List of EventShortDto is started at controller-level (public)");
        return eventServicePublic.findByFilters(eventPublicFilterParams, request);
    }

    @GetMapping("/{id}")
    public EventFullDto findById(@PositiveOrZero @PathVariable Long id, HttpServletRequest request) {
        log.trace("Getting of event with id: {} is started at controller-level (public)", id);
        return eventServicePublic.findById(id, request);
    }
}
