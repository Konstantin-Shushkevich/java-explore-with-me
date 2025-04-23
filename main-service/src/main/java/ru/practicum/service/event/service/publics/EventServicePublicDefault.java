package ru.practicum.service.event.service.publics;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.event.dto.mapper.EventMapper;
import ru.practicum.service.event.dto.params.EventPublicFilterParams;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.model.EventState;
import ru.practicum.service.event.repository.EventRepository;
import ru.practicum.service.event.service.EventStatisticsService;
import ru.practicum.service.exception.EventIsNotInRepositoryException;
import ru.practicum.service.exception.IncorrectRequestException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.practicum.service.event.dto.mapper.EventMapper.toEventShortDto;
import static ru.practicum.service.util.pageable.PageableUtils.getPageable;

@Service
@RequiredArgsConstructor
public class EventServicePublicDefault implements EventServicePublic {

    private final EventRepository eventRepository;
    private final EventStatisticsService eventStatisticsService;

    @Override
    public List<EventShortDto> findByFilters(EventPublicFilterParams eventPublicFilterParams,
                                             HttpServletRequest request) {

        LocalDateTime rangeStart = eventPublicFilterParams.getRangeStart();
        LocalDateTime rangeEnd = eventPublicFilterParams.getRangeEnd();
        String text = eventPublicFilterParams.getText();
        List<Long> categories = eventPublicFilterParams.getCategories();
        String sort = eventPublicFilterParams.getSort();
        Boolean paid = eventPublicFilterParams.getPaid();
        Boolean onlyAvailable = eventPublicFilterParams.getOnlyAvailable();
        Integer from = eventPublicFilterParams.getFrom();
        Integer size = eventPublicFilterParams.getSize();

        if (Objects.isNull(rangeStart)) {
            rangeStart = LocalDateTime.now();
        }

        if (!Objects.isNull(rangeEnd) && rangeStart.isAfter(rangeEnd)) {
            throw new IncorrectRequestException("The start date couldn't be later than the end date");
        }

        if ((!Objects.isNull(text) && text.isBlank()) || (!Objects.isNull(categories) && categories.isEmpty())) {
            return Collections.emptyList();
        }

        eventStatisticsService.saveHit(request);

        return switch (Objects.isNull(sort) ? "default" : sort) {
            case "EVENT_DATE" -> eventRepository.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                            getPageable(from, size, Sort.by("eventDate"))).stream()
                    .map(event -> toEventShortDto(
                            event,
                            eventStatisticsService.getConfirmedRequests(event),
                            eventStatisticsService.getViews(event)))
                    .collect(Collectors.toList());
            case "VIEWS" -> eventRepository.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                            getPageable(from, size, null)).stream()
                    .map(event -> toEventShortDto(
                            event,
                            eventStatisticsService.getConfirmedRequests(event),
                            eventStatisticsService.getViews(event)))
                    .sorted(Comparator.comparing(EventShortDto::getViews).reversed())
                    .collect(Collectors.toList());
            default -> eventRepository.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                            getPageable(from, size, null)).stream()
                    .map(event -> toEventShortDto(
                            event,
                            eventStatisticsService.getConfirmedRequests(event),
                            eventStatisticsService.getViews(event)))
                    .collect(Collectors.toList());
        };
    }

    @Override
    public EventFullDto findById(Long id, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new EventIsNotInRepositoryException("Event with id: " + id + " was not found"));

        eventStatisticsService.saveHit(request);

        return EventMapper.toEventFullDto(
                event,
                eventStatisticsService.getConfirmedRequests(event),
                eventStatisticsService.getViews(event));
    }
}
