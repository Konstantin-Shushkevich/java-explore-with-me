package ru.practicum.service.category.service.admin;

import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.dto.NewCategoryDto;

public interface CategoryServiceAdmin {

    CategoryDto create(NewCategoryDto newCategoryDto);

    void deleteById(Long catId);

    CategoryDto updateById(Long catId, NewCategoryDto newCategoryDtoForPatch);
}
