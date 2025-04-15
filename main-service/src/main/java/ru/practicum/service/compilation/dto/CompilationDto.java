package ru.practicum.service.compilation.dto;

import lombok.Builder;
import ru.practicum.service.event.dto.EventShortDto;

import java.util.Set;

@Builder
public class CompilationDto {

    private Long id;

    private Set<EventShortDto> events;

    private Boolean pinned;

    private String title;
}
