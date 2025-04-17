package ru.practicum.service.compilation.service.publics;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.service.compilation.dto.CompilationDto;
import ru.practicum.service.compilation.model.Compilation;
import ru.practicum.service.compilation.repository.CompilationRepository;
import ru.practicum.service.event.service.admin.EventServiceAdminDefault;
import ru.practicum.service.exception.CompilationIsNotInRepositoryException;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.service.compilation.dto.mapper.CompilationMapper.toCompilationDto;

@Service
@RequiredArgsConstructor
public class CompilationServicePublicDefault implements CompilationServicePublic {

    private final CompilationRepository compilationRepository;
    private final EventServiceAdminDefault eventServiceAdminDefault;

    @Override
    public List<CompilationDto> findAll(Boolean pinned, int from, int size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        return compilationRepository.getCompilations(pinned, pageable).stream()
                .map(comp -> toCompilationDto(comp,
                        eventServiceAdminDefault.toEventShortDtoList(comp.getEvents()))).collect(Collectors.toList());
    }

    @Override
    public CompilationDto findById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationIsNotInRepositoryException("Compilation with id: " + compId +
                        " was not found"));
        return toCompilationDto(compilation, eventServiceAdminDefault.toEventShortDtoList(compilation.getEvents()));
    }
}
