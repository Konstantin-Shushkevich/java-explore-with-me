package ru.practicum.service.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.user.dto.NewUserRequest;
import ru.practicum.service.user.dto.UserDto;
import ru.practicum.service.user.service.UserService;
import ru.practicum.service.util.pageable.PageRequestParams;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto add(@Valid @RequestBody NewUserRequest newUserRequest) {
        log.trace("Adding user with name: {} and email {} is started at controller-level", newUserRequest.getName(),
                newUserRequest.getEmail());
        return userService.add(newUserRequest);
    }

    @GetMapping
    public List<UserDto> getAll(@RequestParam(required = false) List<Long> ids,
                                @Valid @ModelAttribute PageRequestParams pageRequestParams) {
        log.trace("Getting List of all users is started at controller-level");
        return userService.getAll(ids, pageRequestParams);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@Positive @PathVariable Long userId) {
        log.trace("Searching for user to delete in progress at controller-level");
        userService.deleteById(userId);
    }
}
