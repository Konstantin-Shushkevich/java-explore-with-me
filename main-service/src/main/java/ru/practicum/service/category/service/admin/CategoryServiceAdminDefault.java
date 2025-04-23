package ru.practicum.service.category.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.dto.NewCategoryDto;
import ru.practicum.service.category.model.Category;
import ru.practicum.service.category.repository.CategoryRepository;
import ru.practicum.service.event.repository.EventRepository;
import ru.practicum.service.exception.CategoryHasRelatedEventsException;
import ru.practicum.service.exception.CategoryIsNotInRepositoryException;
import ru.practicum.service.exception.CategoryNameNotUniqueException;

import static ru.practicum.service.category.dto.mapper.CategoryMapper.*;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceAdminDefault implements CategoryServiceAdmin {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {

        String name = newCategoryDto.getName();

        checkIfCategoryNameIdNotUnique(name);

        Category category = toCategory(newCategoryDto);

        return toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public void deleteById(Long catId) {

        findCategoryByIdIfExists(catId);

        if (eventRepository.existsByCategoryId(catId)) {
            throw new CategoryHasRelatedEventsException("The category (id: " + catId + ") cannot be deleted: " +
                    "it has events associated with it");
        }

        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateById(Long catId, NewCategoryDto newCategoryDtoForPatch) {

        Category categoryOldVersion = findCategoryByIdIfExists(catId);

        String newName = newCategoryDtoForPatch.getName();

        if (categoryOldVersion.getName().equals(newName)) {
            return toCategoryDto(categoryOldVersion);
        }

        checkIfCategoryNameIdNotUnique(newName);

        categoryOldVersion.setName(newName);

        return toCategoryDto(categoryRepository.save(categoryOldVersion));
    }

    private Category findCategoryByIdIfExists(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryIsNotInRepositoryException("Category with id: " + catId +
                        " is not in repository"));
    }

    private void checkIfCategoryNameIdNotUnique(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new CategoryNameNotUniqueException("Category name: " + name + " is not unique");
        }
    }
}
