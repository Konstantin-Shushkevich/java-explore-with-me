package ru.practicum.service.category.dto.mapper;

import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.dto.NewCategoryDto;
import ru.practicum.service.category.model.Category;

public class CategoryMapper {

    public static Category toCategory(NewCategoryDto newCategoryDto) {
        return Category.builder()
                .name(newCategoryDto.getName())
                .build();
    }

    public static CategoryDto toCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
