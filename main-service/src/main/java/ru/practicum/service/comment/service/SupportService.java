package ru.practicum.service.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.repository.EventRepository;
import ru.practicum.service.exception.EventIsNotInRepositoryException;
import ru.practicum.service.exception.InvalidCommentTimeFiltersRequest;
import ru.practicum.service.exception.UserIsNotInRepositoryException;
import ru.practicum.service.user.model.User;
import ru.practicum.service.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Objects;

@RequiredArgsConstructor
public abstract class SupportService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    protected User findUserByIdIfExists(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserIsNotInRepositoryException(
                "User with id: " + userId + " was not found"));
    }

    protected Event findEventByIdIfExists(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new EventIsNotInRepositoryException(
                "Event with id: " + eventId + " was not found"));
    }

    protected void validateTimeFilters(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (!Objects.isNull(rangeStart) && !Objects.isNull(rangeEnd) && rangeStart.isAfter(rangeEnd)) {
            throw new InvalidCommentTimeFiltersRequest("The start date must not be later than the end date");
        }
    }

    protected Pageable getPageable(int from, int size, String sort) {
        int page = from / size;

        Sort sorting = sort.equals("OLD") ? Sort.by("createdOn").ascending()
                : Sort.by("createdOn").descending();

        return PageRequest.of(page, size, sorting);
    }
}
