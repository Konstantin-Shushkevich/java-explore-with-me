package ru.practicum.service.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.event.service.publics.EventServicePublic;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.service.util.Constant.DATE_TIME_FORMATTER;

@Slf4j
@Validated
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventPublicController {

    private final EventServicePublic eventServicePublic;

    @GetMapping
    public List<EventShortDto> findByFilters(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = DATE_TIME_FORMATTER) LocalDateTime rangeStart,
            @RequestParam(required = false)
            @DateTimeFormat(pattern = DATE_TIME_FORMATTER) LocalDateTime rangeEnd,
            @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(required = false) @Pattern(regexp = "EVENT_DATE|VIEWS") String sort,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        log.trace("Getting List of EventShortDto is started at controller-level (public)");
        return eventServicePublic.findByFilters(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                from, size, request);
    }

    @GetMapping("/{id}")
    public EventFullDto findById(@PositiveOrZero @PathVariable Long id, HttpServletRequest request) {
        log.trace("Getting of event with id: {} is started at controller-level (public)", id);
        return eventServicePublic.findById(id, request);
    }
}
