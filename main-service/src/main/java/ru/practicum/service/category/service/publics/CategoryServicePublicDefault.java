package ru.practicum.service.category.service.publics;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.dto.mapper.CategoryMapper;
import ru.practicum.service.category.model.Category;
import ru.practicum.service.category.repository.CategoryRepository;
import ru.practicum.service.exception.CategoryIsNotInRepositoryException;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.service.category.dto.mapper.CategoryMapper.toCategoryDto;

@Service
@RequiredArgsConstructor
public class CategoryServicePublicDefault implements CategoryServicePublic {

    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryDto> findByCondition(Integer from, Integer size) {
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        return categoryRepository.findAll(pageable).stream()
                .map(CategoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto findById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() ->
                new CategoryIsNotInRepositoryException("There's no category with id : " + catId + " in repository"));
        return toCategoryDto(category);
    }
}
