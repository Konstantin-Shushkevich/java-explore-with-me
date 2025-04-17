package ru.practicum.service.compilation.dto;

import lombok.*;
import ru.practicum.service.event.dto.EventShortDto;

import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {

    private Long id;

    private Set<EventShortDto> events;

    private Boolean pinned;

    private String title;
}
