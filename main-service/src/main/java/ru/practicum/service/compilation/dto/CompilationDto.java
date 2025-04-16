package ru.practicum.service.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.practicum.service.event.dto.EventShortDto;

import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {

    private Long id;

    private Set<EventShortDto> events;

    private Boolean pinned;

    private String title;
}
