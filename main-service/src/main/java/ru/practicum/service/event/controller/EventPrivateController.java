package ru.practicum.service.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.event.dto.*;
import ru.practicum.service.event.service.nonpublic.EventServicePrivate;
import ru.practicum.service.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.service.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.service.request.dto.ParticipationRequestDto;
import ru.practicum.service.util.pageable.PageRequestParams;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
public class EventPrivateController {

    private final EventServicePrivate eventService;

    @GetMapping
    public List<EventShortDto> getByAuthor(
            @Positive @PathVariable Long userId,
            @Valid @ModelAttribute PageRequestParams pageRequestParams) {
        log.trace("Getting List of EventShortDto by author is started at controller-level");
        return eventService.getByAuthor(userId, pageRequestParams);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto add(@Positive @PathVariable Long userId,
                            @Valid @RequestBody NewEventDto newEventDto) {
        log.trace("Adding event by user with: {} is started at controller-level", userId);
        return eventService.add(userId, newEventDto);
    }

    @GetMapping("/{eventId}")
    public EventFullDto findById(@Positive @PathVariable Long userId,
                                 @Positive @PathVariable Long eventId) {
        log.trace("Getting EventFullDto (userId: {}, eventId: {}) is started at controller-level", userId, eventId);
        return eventService.findById(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateById(@Positive @PathVariable Long userId,
                                   @Positive @PathVariable Long eventId,
                                   @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.trace("Updating event with id: {} by user with id: {} is started at controller-level (private)",
                eventId, userId);
        return eventService.updateById(userId, eventId, updateEventUserRequest);
    }

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getRequests(@Positive @PathVariable Long userId,
                                                     @Positive @PathVariable Long eventId) {
        log.trace("Getting requests for event with id: {} by user with id: {} is started at controller-level (private)",
                eventId, userId);
        return eventService.getRequests(userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(@Positive @PathVariable Long userId,
                                                              @Positive @PathVariable Long eventId,
                                                              @Valid @RequestBody
                                                              EventRequestStatusUpdateRequest request) {
        log.trace("Update of event (id: {}) status by user with id: {} is started at controller-level (private)",
                eventId, userId);
        return eventService.updateRequestStatus(userId, eventId, request);
    }
}
