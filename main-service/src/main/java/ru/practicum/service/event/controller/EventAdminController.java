package ru.practicum.service.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.UpdateEventAdminRequest;
import ru.practicum.service.event.model.EventState;
import ru.practicum.service.event.service.admin.EventServiceAdmin;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.service.util.Constant.DATE_TIME_FORMATTER;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventAdminController {

    private final EventServiceAdmin eventServiceAdmin;

    @GetMapping
    public List<EventFullDto> findAllByCriteria(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventState> states,
            @RequestParam(required = false) List<Long> categories,
            @DateTimeFormat(pattern = DATE_TIME_FORMATTER)
            @RequestParam(required = false) LocalDateTime rangeStart,
            @DateTimeFormat(pattern = DATE_TIME_FORMATTER)
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size) {
        log.trace("Getting List of EventFullDto by criteria is started at controller-level");
        return eventServiceAdmin.findAllByCriteria(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateById(@Positive @PathVariable Long eventId,
                                   @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.trace("Updating of event with id: {} is started at controller-level", eventId);
        return eventServiceAdmin.updateById(eventId, updateEventAdminRequest);
    }
}
