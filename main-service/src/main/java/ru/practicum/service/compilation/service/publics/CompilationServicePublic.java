package ru.practicum.service.compilation.service.publics;

import ru.practicum.service.compilation.dto.CompilationDto;
import ru.practicum.service.util.pageable.PageRequestParams;

import java.util.List;

public interface CompilationServicePublic {
    List<CompilationDto> findAll(Boolean pinned, PageRequestParams pageRequestParams);

    CompilationDto findById(Long compId);
}
