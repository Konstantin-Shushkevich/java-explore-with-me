package ru.practicum.service.util.pageable;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestParams {

    @PositiveOrZero
    private Integer from = 0;

    @Positive
    private Integer size = 10;
}
