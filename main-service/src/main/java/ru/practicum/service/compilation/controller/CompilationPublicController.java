package ru.practicum.service.compilation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.compilation.dto.CompilationDto;
import ru.practicum.service.compilation.service.publics.CompilationServicePublic;
import ru.practicum.service.util.pageable.PageRequestParams;

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
                                        @Valid @ModelAttribute PageRequestParams pageRequestParams) {
        log.trace("Getting List of CompilationDto is started at controller-level (public)");
        return compilationServicePublic.findAll(pinned, pageRequestParams);
    }

    @GetMapping("/{compId}")
    public CompilationDto findById(@Positive @PathVariable Long compId) {
        return compilationServicePublic.findById(compId);
    }
}
