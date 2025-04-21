package ru.practicum.service.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.exception.UserEmailAlreadyInRepositoryException;
import ru.practicum.service.exception.UserIsNotInRepositoryException;
import ru.practicum.service.user.dto.NewUserRequest;
import ru.practicum.service.user.dto.UserDto;
import ru.practicum.service.user.dto.mapper.UserMapper;
import ru.practicum.service.user.model.User;
import ru.practicum.service.user.repository.UserRepository;
import ru.practicum.service.util.pageable.PageRequestParams;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.service.util.pageable.PageableUtils.getPageable;
import static ru.practicum.service.user.dto.mapper.UserMapper.toUser;
import static ru.practicum.service.user.dto.mapper.UserMapper.toUserDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto add(NewUserRequest newUserRequest) {

        String email = newUserRequest.getEmail();

        if (userRepository.existsByEmail(email)) {
            throw new UserEmailAlreadyInRepositoryException("Trying to save email that has already been in repository");
        }

        User user = toUser(newUserRequest);

        return toUserDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> getAll(List<Long> ids, PageRequestParams pageRequestParams) {

        if (Objects.nonNull(ids) && !ids.isEmpty()) {
            return userRepository.findUsersByIdIn(ids).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }

        Pageable pageable = getPageable(pageRequestParams, Sort.by("id"));

        return userRepository.findAll(pageable).stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new UserIsNotInRepositoryException("User to delete not found in repository");
        }

        userRepository.deleteById(userId);
    }
}
