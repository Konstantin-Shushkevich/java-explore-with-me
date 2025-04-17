package ru.practicum.service.compilation.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.compilation.dto.CompilationDto;
import ru.practicum.service.compilation.service.publics.CompilationServicePublic;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationPublicController {

    private final CompilationServicePublic compilationServicePublic;

    @GetMapping
    public List<CompilationDto> findAll(@RequestParam(required = false) Boolean pinned,
                                        @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                        @Positive @RequestParam(defaultValue = "10") int size) {
        log.trace("Getting List of CompilationDto is started at controller-level (public)");
        return compilationServicePublic.findAll(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto findById(@Positive @PathVariable Long compId) {
        return compilationServicePublic.findById(compId);
    }
}
