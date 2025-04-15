package ru.practicum.service.request.dto;

import lombok.Builder;

import java.util.List;

@Builder
public class EventRequestStatusUpdateResult {
    private List<ParticipationRequestDto> confirmedRequests;
    private List<ParticipationRequestDto> rejectedRequests;
}
