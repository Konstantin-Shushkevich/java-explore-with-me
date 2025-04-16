package ru.practicum.service.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.service.request.model.ParticipationRequestStatus;

import java.time.LocalDateTime;

import static ru.practicum.service.util.Constant.OTHER_DATE_TIME_FORMATTER;

@Builder
@Getter
public class ParticipationRequestDto {
    private Long id;

    @JsonFormat(pattern = OTHER_DATE_TIME_FORMATTER)
    private LocalDateTime created;

    private Long event;

    private Long requester;

    private ParticipationRequestStatus status;
}
