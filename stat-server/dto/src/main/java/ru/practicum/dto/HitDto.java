package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class HitDto {

    @NotBlank(message = "Hit's app is NULL or EMPTY")
    private String app;

    @NotBlank(message = "Hit's uri is NULL or EMPTY")
    private String uri;

    @NotBlank(message = "Hit's ip is NULL or EMPTY")
    private String ip;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
