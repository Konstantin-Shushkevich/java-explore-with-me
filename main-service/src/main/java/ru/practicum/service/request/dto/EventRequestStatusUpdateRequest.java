package ru.practicum.service.request.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.service.exception.IncorrectRequestException;
import ru.practicum.service.request.model.ParticipationRequestStatus;

import java.util.List;

@Data
public class EventRequestStatusUpdateRequest {

    @NotEmpty
    private List<Long> requestIds;

    @NotNull
    private ParticipationRequestStatus status;

    @AssertTrue
    private boolean isValidStatus() {
        if (!(status.equals(ParticipationRequestStatus.CONFIRMED)
                || status.equals(ParticipationRequestStatus.REJECTED))) {
            throw new IncorrectRequestException("Field: status. Error: must be CONFIRMED or REJECTED. " +
                    "Value: " + status);
        }

        return true;
    }
}
