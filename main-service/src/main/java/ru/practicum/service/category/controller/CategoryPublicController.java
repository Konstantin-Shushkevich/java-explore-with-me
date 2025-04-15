package ru.practicum.service.category.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.service.publics.CategoryServicePublic;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryPublicController {

    private final CategoryServicePublic categoryService;

    @GetMapping
    List<CategoryDto> findByCondition(@PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                      @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.trace("Getting List of categories selected by condition (from: {}, size: {}) is started at controller-level",
                from, size);
        return categoryService.findByCondition(from, size);
    }

    @GetMapping("/{catId}")
    CategoryDto findById(@Positive @PathVariable Long catId) {
        log.trace("Getting category by id: {} is started at controller-level", catId);
        return categoryService.findById(catId);
    }
}
