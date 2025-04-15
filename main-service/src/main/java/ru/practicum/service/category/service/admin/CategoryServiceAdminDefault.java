package ru.practicum.service.category.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.category.dto.CategoryDto;
import ru.practicum.service.category.dto.NewCategoryDto;
import ru.practicum.service.category.model.Category;
import ru.practicum.service.category.repository.CategoryRepository;
import ru.practicum.service.exception.CategoryHasRelatedEventsException;
import ru.practicum.service.exception.CategoryIsNotInRepositoryException;
import ru.practicum.service.exception.CategoryNameNotUniqueException;

import static ru.practicum.service.category.dto.mapper.CategoryMapper.*;

@Service
@RequiredArgsConstructor
public class CategoryServiceAdminDefault implements CategoryServiceAdmin {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        try {
            return toCategoryDto(categoryRepository.save(toCategory(newCategoryDto)));
        } catch (DataIntegrityViolationException e) {
            throw new CategoryNameNotUniqueException("Trying to add a category that already exists in the database");
        }
    }

    @Override
    public void deleteById(Long catId) {
        try {
            categoryRepository.deleteById(catId);
        } catch (EmptyResultDataAccessException e) {
            throw new CategoryIsNotInRepositoryException("Category to delete not found in repository");
        } catch (DataIntegrityViolationException e) {
            throw new CategoryHasRelatedEventsException("The category cannot be deleted: " +
                    "it has events associated with it");
        }
    }

    @Override
    @Transactional
    public CategoryDto updateById(Long catId, NewCategoryDto newCategoryDtoForPatch) {

        Category categoryOldVersion = categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryIsNotInRepositoryException("Category for update not found in repository"));

        String newName = newCategoryDtoForPatch.getName();

        if (categoryOldVersion.getName().equals(newName)) {
            return toCategoryDto(categoryOldVersion);
        }

        if (categoryRepository.existsByName(newName)) {
            throw new CategoryNameNotUniqueException("Category name must be unique");
        }

        categoryOldVersion.setName(newName);

        return toCategoryDto(categoryRepository.save(categoryOldVersion));
    }
}
