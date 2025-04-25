package ru.practicum.service.comment.dto.params;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

import static ru.practicum.service.util.Constant.DATE_TIME_FORMATTER;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommentPrivateFilterParams {

    @DateTimeFormat(pattern = DATE_TIME_FORMATTER)
    LocalDateTime rangeStart;

    @DateTimeFormat(pattern = DATE_TIME_FORMATTER)
    LocalDateTime rangeEnd;

    @PositiveOrZero
    int from = 0;

    @Positive
    int size = 10;

    @Pattern(regexp = "RECENT|OLD")
    String sort = "OLD";
}
