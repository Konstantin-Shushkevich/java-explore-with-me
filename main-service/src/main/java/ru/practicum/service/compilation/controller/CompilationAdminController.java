package ru.practicum.service.compilation.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.compilation.dto.CompilationDto;
import ru.practicum.service.compilation.dto.NewCompilationDto;
import ru.practicum.service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.service.compilation.service.admin.CompilationServiceAdmin;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController {

    private final CompilationServiceAdmin compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto add(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        log.trace("Adding compilation is started at controller-level (admin)");
        return compilationService.add(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Positive @PathVariable Long compId) {
        log.trace("Delete of compilation wit id: {} is started at controller-level (admin)", compId);
        compilationService.deleteById(compId);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateById(@Positive @PathVariable Long compId,
                                     @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        log.trace("Update of compilation with id: {} is started at controller-level (admin)", compId);
        return compilationService.updateById(compId, updateCompilationRequest);
    }
}
