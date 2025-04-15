package ru.practicum.service.compilation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.Set;

@Builder
@Getter
public class UpdateCompilationRequest {
    @UniqueElements
    private Set<Long> events;

    @NotNull
    private Boolean pinned;

    @Size(min = 1, max = 50)
    private String title;
}
