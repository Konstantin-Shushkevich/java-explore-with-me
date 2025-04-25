package ru.practicum.service.comment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.service.user.dto.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.service.util.Constant.DATE_TIME_FORMATTER;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {

    private Long id;

    private String text;

    private UserShortDto author;

    private Long eventId;

    @JsonFormat(pattern = DATE_TIME_FORMATTER)
    private LocalDateTime createdOn;

    @JsonFormat(pattern = DATE_TIME_FORMATTER)
    private LocalDateTime updatedOn;
}
