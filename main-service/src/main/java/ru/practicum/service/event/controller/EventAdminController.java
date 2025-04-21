package ru.practicum.service.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.UpdateEventAdminRequest;
import ru.practicum.service.event.dto.params.EventAdminFilterParams;
import ru.practicum.service.event.service.admin.EventServiceAdmin;

import java.util.List;


@Slf4j
@Validated
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class EventAdminController {

    private final EventServiceAdmin eventServiceAdmin;

    @GetMapping
    public List<EventFullDto> findAllByCriteria(@Valid @ModelAttribute EventAdminFilterParams eventAdminFilterParams) {
        log.trace("Getting List of EventFullDto by criteria is started at controller-level");
        return eventServiceAdmin.findAllByCriteria(eventAdminFilterParams);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateById(@Positive @PathVariable Long eventId,
                                   @Valid @RequestBody UpdateEventAdminRequest updateEventAdminRequest) {
        log.trace("Updating of event with id: {} is started at controller-level", eventId);
        return eventServiceAdmin.updateById(eventId, updateEventAdminRequest);
    }
}
