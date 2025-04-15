package ru.practicum.service.compilation.service.publics;

import ru.practicum.service.compilation.dto.CompilationDto;

import java.util.List;

public interface CompilationServicePublic {
    List<CompilationDto> findAll(Boolean pinned, int from, int size);

    CompilationDto findById(Long compId);
}
