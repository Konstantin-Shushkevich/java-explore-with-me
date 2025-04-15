package ru.practicum.service.request.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.service.event.model.EventState;

import java.util.List;

@Builder
@Getter
public class EventRequestStatusUpdateRequest {
    private String description;
    private List<Long> requestIds;
    private EventState status;
}
