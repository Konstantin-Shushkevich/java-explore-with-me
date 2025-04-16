package ru.practicum.service.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class NewCategoryDto {

    @NotBlank(message = "Category name is NULL or EMPTY")
    @Size(min = 1, max = 50, message = "Category name has incorrect size")
    private String name;
}
