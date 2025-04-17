package ru.practicum.service.compilation.service.admin;

import ru.practicum.service.compilation.dto.CompilationDto;
import ru.practicum.service.compilation.dto.NewCompilationDto;
import ru.practicum.service.compilation.dto.UpdateCompilationRequest;

public interface CompilationServiceAdmin {
    CompilationDto add(NewCompilationDto newCompilationDto);

    void deleteById(Long compId);

    CompilationDto updateById(Long compId, UpdateCompilationRequest updateCompilationRequest);
}
