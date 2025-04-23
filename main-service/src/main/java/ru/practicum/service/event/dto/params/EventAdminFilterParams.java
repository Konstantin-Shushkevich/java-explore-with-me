package ru.practicum.service.event.dto.params;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.service.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.service.util.Constant.DATE_TIME_FORMATTER;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventAdminFilterParams {

    private List<Long> users;

    private List<EventState> states;

    private List<Long> categories;

    @DateTimeFormat(pattern = DATE_TIME_FORMATTER)
    private LocalDateTime rangeStart;

    @DateTimeFormat(pattern = DATE_TIME_FORMATTER)
    private LocalDateTime rangeEnd;

    @PositiveOrZero
    private Integer from = 0;

    @Positive
    private Integer size = 10;
}
