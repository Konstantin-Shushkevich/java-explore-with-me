package ru.practicum.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class HitDto {

    @NotBlank(message = "Not able to add hit if app is NULL/EMPTY")
    private String app;

    @NotBlank(message = "Not able to add hit if uri is NULL/EMPTY")
    private String uri;

    @NotBlank(message = "Not able to add hit if ip is NULL/EMPTY")
    private String ip;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
}
