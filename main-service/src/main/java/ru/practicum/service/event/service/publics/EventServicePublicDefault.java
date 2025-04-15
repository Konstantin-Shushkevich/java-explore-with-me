package ru.practicum.service.event.service.publics;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatClient;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.event.dto.mapper.EventMapper;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.model.EventState;
import ru.practicum.service.event.repository.EventRepository;
import ru.practicum.service.exception.EventIsNotInRepositoryException;
import ru.practicum.service.exception.IncorrectRequestException;
import ru.practicum.service.exception.StatisticServerException;
import ru.practicum.service.request.model.ParticipationRequestStatus;
import ru.practicum.service.request.repository.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.service.event.dto.mapper.EventMapper.toEventShortDto;

@Service
@RequiredArgsConstructor
public class EventServicePublicDefault implements EventServicePublic {

    private final EventRepository eventRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatClient statClient;

    @Override
    public List<EventShortDto> findByFilters(String text,
                                             List<Long> categories,
                                             Boolean paid,
                                             LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd,
                                             Boolean onlyAvailable,
                                             String sort,
                                             int from,
                                             int size,
                                             HttpServletRequest request) {
        if (Objects.isNull(rangeStart)) {
            rangeStart = LocalDateTime.now();
        }
        if (!Objects.isNull(rangeEnd) && rangeStart.isAfter(rangeEnd)) {
            throw new IncorrectRequestException("The start date couldn't be later than the end date");
        }
        if ((!Objects.isNull(text) && text.isBlank()) || (!Objects.isNull(categories) && categories.isEmpty())) {
            return Collections.emptyList();
        }
        saveHit(request);
        switch (Objects.isNull(sort) ? "default" : sort) {
            case "EVENT_DATE":
                return eventRepository.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                                getPageable(from, size, Sort.by("eventDate"))).stream()
                        .map(event -> toEventShortDto(event, getConfirmedRequests(event), getViews(event)))
                        .collect(Collectors.toList());
            case "VIEWS":
                return eventRepository.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                                getPageable(from, size, null)).stream()
                        .map(event -> toEventShortDto(event, getConfirmedRequests(event), getViews(event)))
                        .sorted(Comparator.comparing(EventShortDto::getViews).reversed())
                        .collect(Collectors.toList());
            default:
                return eventRepository.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                                getPageable(from, size, null)).stream()
                        .map(event -> toEventShortDto(event, getConfirmedRequests(event), getViews(event)))
                        .collect(Collectors.toList());
        }
    }

    @Override
    public EventFullDto findById(Long id, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new EventIsNotInRepositoryException("Event with id: " + id + " was not found"));
        saveHit(request);
        return EventMapper.toEventFullDto(event, getConfirmedRequests(event), getViews(event));
    }

    private Pageable getPageable(int from, int size, Sort sort) {
        int page = from / size;
        return Objects.isNull(sort) ? PageRequest.of(page, size) : PageRequest.of(page, size, sort);
    }

    private Integer getConfirmedRequests(Event event) {
        return event.getState().equals(EventState.PUBLISHED) ?
                participationRequestRepository.countAllByEventIdAndStatus(event.getId(),
                        ParticipationRequestStatus.CONFIRMED) : 0;
    }

    private Long getViews(Event event) {
        if (!event.getState().equals(EventState.PUBLISHED)) {
            return 0L;
        }
        List<StatsDto> statsDto;
        try {
            statsDto = statClient.readStats(
                    event.getPublishedOn(),
                    LocalDateTime.now(),
                    List.of(String.format("/events/%d", event.getId())),
                    true
            );
        } catch (Throwable e) {
            throw new StatisticServerException("Error at the statistic-client");
        }
        return statsDto.isEmpty() ? 0L : statsDto.getFirst().getHits();
    }

    private void saveHit(HttpServletRequest request) {
        ResponseEntity<Object> response = statClient.addHit(
                HitDto.builder()
                        .app("ewm-main-service")
                        .uri(request.getRequestURI())
                        .ip(request.getRemoteAddr())
                        .timestamp(LocalDateTime.now())
                        .build());
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new StatisticServerException("Error at the statistics-client");
        }
    }
}
