package ru.practicum.service.exception.handler;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import static ru.practicum.service.util.Constant.DATE_TIME_FORMATTER;

@Getter
@Builder
@RequiredArgsConstructor
public class ApiError {
    private final String message;
    private final String reason;
    private final String status;
    @JsonFormat(pattern = DATE_TIME_FORMATTER)
    private final LocalDateTime timestamp;
}
