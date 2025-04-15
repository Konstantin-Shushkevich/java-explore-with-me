package ru.practicum.service.request.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.request.dto.ParticipationRequestDto;
import ru.practicum.service.request.service.ParticipationRequestService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class ParticipationRequestPrivateController {

    private final ParticipationRequestService participationRequestService;

    @GetMapping
    public List<ParticipationRequestDto> findAllForUserByFilters(@Positive @PathVariable Long userId) {
        log.trace("findAllForUserByFilters method ia started at controller-level (ParticipationRequestPrivate)");
        return participationRequestService.findAllForUserByFilters(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto add(@Positive @PathVariable Long userId, @Positive @RequestParam Long eventId) {
        log.trace("Adding ParticipationRequest is started at controller-level (ParticipationRequestPrivate)");
        return participationRequestService.add(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto updateStatus(@Positive @PathVariable Long userId,
                                                @Positive @PathVariable Long requestId) {
        log.trace("Updating status of ParticipationRequest is started at controller-level (ParticipationRequestPrivate)");
        return participationRequestService.updateStatus(userId, requestId);
    }
}
