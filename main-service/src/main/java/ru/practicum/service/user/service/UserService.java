package ru.practicum.service.user.service;

import ru.practicum.service.user.dto.NewUserRequest;
import ru.practicum.service.user.dto.UserDto;
import ru.practicum.service.util.pageable.PageRequestParams;

import java.util.List;

public interface UserService {
    UserDto add(NewUserRequest newUserRequest);

    List<UserDto> getAll(List<Long> ids, PageRequestParams pageRequestParams);

    void deleteById(Long userId);
}
