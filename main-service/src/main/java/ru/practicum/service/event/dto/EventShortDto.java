package ru.practicum.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.service.util.Constant.DATE_TIME_FORMATTER;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventShortDto {
    private Long id;

    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    @JsonFormat(pattern = DATE_TIME_FORMATTER)
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private Boolean paid;

    private String title;

    private Long views;
}
