package ru.practicum.service.request.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.service.request.model.ParticipationRequestStatus;

import java.time.LocalDateTime;

@Builder
@Getter
public class ParticipationRequestDto {
    private Long id;

    private LocalDateTime created;

    private Long event;

    private Long requester;

    private ParticipationRequestStatus status;
}
