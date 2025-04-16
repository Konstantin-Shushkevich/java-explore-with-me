package ru.practicum.service.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.service.exception.UserEmailAlreadyInRepositoryException;
import ru.practicum.service.exception.UserIsNotInRepositoryException;
import ru.practicum.service.user.dto.NewUserRequest;
import ru.practicum.service.user.dto.UserDto;
import ru.practicum.service.user.dto.mapper.UserMapper;
import ru.practicum.service.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.service.user.dto.mapper.UserMapper.toUser;
import static ru.practicum.service.user.dto.mapper.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto add(NewUserRequest newUserRequest) {
        try {
            return toUserDto(userRepository.save(toUser(newUserRequest)));
        } catch (DataIntegrityViolationException e) {
            throw new UserEmailAlreadyInRepositoryException("Trying to save email that has already been in repository");
        }
    }

    @Override
    public List<UserDto> getAll(List<Long> ids, int from, int size) {
        if (Objects.nonNull(ids) && !ids.isEmpty()) {
            return userRepository.findUsersByIdIn(ids).stream()
                    .map(UserMapper::toUserDto)
                    .collect(Collectors.toList());
        }
        int page = from / size;
        Pageable pageable = PageRequest.of(page, size, Sort.by("id"));
        return userRepository.findAll(pageable).stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Long userId) {
        try {
            userRepository.deleteById(userId);
        } catch (EmptyResultDataAccessException e) {
            throw new UserIsNotInRepositoryException("User to delete not found in repository");
        }
    }
}
