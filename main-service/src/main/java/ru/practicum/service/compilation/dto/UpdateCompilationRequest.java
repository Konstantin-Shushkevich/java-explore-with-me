package ru.practicum.service.compilation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.Set;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateCompilationRequest {
    @UniqueElements
    private Set<Long> events;

    @NotNull
    private Boolean pinned;

    @Size(min = 1, max = 50)
    private String title;
}
