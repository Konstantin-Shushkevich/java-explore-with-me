package ru.practicum.service.category.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.dto.NewCategoryDto;
import ru.practicum.service.category.service.admin.CategoryServiceAdmin;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {

    private final CategoryServiceAdmin categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.trace("Adding category (name = {}) is started", newCategoryDto.getName());
        return categoryService.create(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@Positive @PathVariable Long catId) {
        log.trace("Deletion of category with catId: {} is started", catId);
        categoryService.deleteById(catId);
    }

    @PatchMapping("/{catId}")
    public CategoryDto updateById(@Positive @PathVariable Long catId,
                                  @Valid @RequestBody NewCategoryDto newCategoryDtoForPatch) {
        log.trace("Updating (PATCH) of category with catId: {} is started", catId);
        return categoryService.updateById(catId, newCategoryDtoForPatch);
    }
}
