package ru.practicum.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.practicum.service.event.dto.action.UserStateAction;
import ru.practicum.service.event.model.Location;

import java.time.LocalDateTime;

import static ru.practicum.service.util.Constant.DATE_TIME_FORMATTER;

@Data
public class UpdateEventUserRequest {

    @Size(min = 20, max = 2000)
    private String annotation;

    @Positive
    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = DATE_TIME_FORMATTER)
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    private UserStateAction stateAction;

    @Size(min = 3, max = 120)
    private String title;
}
