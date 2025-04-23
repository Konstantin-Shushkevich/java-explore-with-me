package ru.practicum.service.category.service.publics;

import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.util.pageable.PageRequestParams;

import java.util.List;

public interface CategoryServicePublic {
    List<CategoryDto> findByCondition(PageRequestParams pageRequestParams);

    CategoryDto findById(Long catId);
}
