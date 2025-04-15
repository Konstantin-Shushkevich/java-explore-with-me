package ru.practicum.service.category.service.publics;

import ru.practicum.service.category.dto.CategoryDto;

import java.util.List;

public interface CategoryServicePublic {
    List<CategoryDto> findByCondition(Integer from, Integer size);

    CategoryDto findById(Long catId);
}
