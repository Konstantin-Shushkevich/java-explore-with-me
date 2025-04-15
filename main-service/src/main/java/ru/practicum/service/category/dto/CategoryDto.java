package ru.practicum.service.category.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CategoryDto {

    private Long id;

    private String name;
}
