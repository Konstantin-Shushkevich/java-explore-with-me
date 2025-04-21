package ru.practicum.service.util.pageable;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;

public class PageableUtils {
    public static Pageable getPageable(int from, int size, Sort sort) {
        int page = from / size;
        return Objects.isNull(sort) ? PageRequest.of(page, size) : PageRequest.of(page, size, sort);
    }

    public static Pageable getPageable(PageRequestParams pageRequestParams, Sort sort) {
        int from = pageRequestParams.getFrom();
        int size = pageRequestParams.getSize();
        int page = from / size;
        return Objects.isNull(sort) ? PageRequest.of(page, size) : PageRequest.of(page, size, sort);

    }
}
