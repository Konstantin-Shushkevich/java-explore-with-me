package ru.practicum.service.event.service.nonpublic;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.category.model.Category;
import ru.practicum.service.category.repository.CategoryRepository;
import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.event.dto.NewEventDto;
import ru.practicum.service.event.dto.UpdateEventUserRequest;
import ru.practicum.service.event.dto.mapper.EventMapper;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.model.EventState;
import ru.practicum.service.event.model.Location;
import ru.practicum.service.event.repository.EventRepository;
import ru.practicum.service.event.service.EventStatisticsService;
import ru.practicum.service.exception.*;
import ru.practicum.service.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.service.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.service.request.dto.ParticipationRequestDto;
import ru.practicum.service.request.dto.mapper.ParticipationRequestMapper;
import ru.practicum.service.request.model.ParticipationRequest;
import ru.practicum.service.request.model.ParticipationRequestStatus;
import ru.practicum.service.request.repository.ParticipationRequestRepository;
import ru.practicum.service.user.model.User;
import ru.practicum.service.user.repository.UserRepository;
import ru.practicum.service.util.pageable.PageRequestParams;

import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.service.event.dto.mapper.EventMapper.*;
import static ru.practicum.service.util.pageable.PageableUtils.getPageable;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServicePrivateDefault implements EventServicePrivate {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final EventStatisticsService eventStatisticsService;

    @Override
    public List<EventShortDto> getByAuthor(Long userId, PageRequestParams pageRequestParams) {

        findUserIfExists(userId);

        Pageable pageable = getPageable(pageRequestParams, Sort.by("id").ascending());

        Page<Event> events = eventRepository.findByInitiatorIdWithDetails(userId, pageable);

        return events.map(event -> toEventShortDto(
                event,
                eventStatisticsService.getConfirmedRequests(event),
                eventStatisticsService.getViews(event))).getContent();
    }

    @Override
    @Transactional
    public EventFullDto add(Long userId, NewEventDto newEventDto) {

        User user = findUserIfExists(userId);

        Category category = findCategoryIfExists(newEventDto.getCategory());

        return toEventFullDto(eventRepository.save(toEvent(newEventDto, category, user)), 0, 0L);
    }

    @Override
    public EventFullDto findById(Long userId, Long eventId) {
        Event event = findEventByIdAndInitiatorIdIfExists(eventId, userId);

        return EventMapper.toEventFullDto(
                event,
                eventStatisticsService.getConfirmedRequests(event),
                eventStatisticsService.getViews(event));
    }

    @Override
    @Transactional
    public EventFullDto updateById(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {

        findUserIfExists(userId);

        Event eventFromRepository = findEventByIdAndInitiatorIdIfExists(eventId, userId);

        if (eventFromRepository.getState().equals(EventState.PUBLISHED)) {
            throw new ViolationOfTermsException("Only PENDING or CANCELED events can be changed");
        }

        Category category = eventFromRepository.getCategory();

        if (!Objects.isNull(updateEventUserRequest.getCategory())) {
            category = findCategoryIfExists(updateEventUserRequest.getCategory());
        }

        Location location = Objects.isNull(updateEventUserRequest.getLocation()) ?
                eventFromRepository.getLocation() : updateEventUserRequest.getLocation();
        Event newEvent = EventMapper.toEvent(updateEventUserRequest, eventFromRepository, category, location);

        return EventMapper.toEventFullDto(
                eventRepository.save(newEvent),
                eventStatisticsService.getConfirmedRequests(newEvent),
                eventStatisticsService.getViews(newEvent));
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {

        findUserIfExists(userId);

        findEventByIdAndInitiatorIdIfExists(eventId, userId);

        return participationRequestRepository.findByEventId(eventId).stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestStatus(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {

        findUserIfExists(userId);

        Event event = findEventByIdAndInitiatorIdIfExists(eventId, userId);

        if (!event.getRequestModeration()) {
            throw new ViolationOfTermsException("For an event with id: " + eventId
                    + " confirmation of requests  is not required");
        }

        int available = event.getParticipantLimit() - eventStatisticsService.getConfirmedRequests(event);

        if (available <= 0) {
            throw new ViolationOfTermsException("The participant limit has been reached");
        }

        List<ParticipationRequest> requests = participationRequestRepository.findByEventIdAndStatusAndIdIn(eventId,
                ParticipationRequestStatus.PENDING, eventRequestStatusUpdateRequest.getRequestIds());

        if (requests.size() < eventRequestStatusUpdateRequest.getRequestIds().size()) {
            throw new ViolationOfTermsException("The status can only be changed for PENDING requests");
        }

        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();

        for (ParticipationRequest request : requests) {
            if (eventRequestStatusUpdateRequest.getStatus().equals(ParticipationRequestStatus.CONFIRMED)
                    && available > 0) {
                request.setStatus(ParticipationRequestStatus.CONFIRMED);
                confirmedRequests.add(request);
                available--;
            } else {
                request.setStatus(ParticipationRequestStatus.REJECTED);
                rejectedRequests.add(request);
            }
        }

        participationRequestRepository.saveAll(confirmedRequests);
        participationRequestRepository.saveAll(rejectedRequests);

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests.stream()
                        .map(ParticipationRequestMapper::toParticipationRequestDto).collect(Collectors.toList()))
                .rejectedRequests(rejectedRequests.stream()
                        .map(ParticipationRequestMapper::toParticipationRequestDto).collect(Collectors.toList()))
                .build();
    }

    private User findUserIfExists(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserIsNotInRepositoryException("User with id: " + userId + " was not found"));
    }

    private Category findCategoryIfExists(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new CategoryIsNotInRepositoryException("Category with id: " + catId
                        + " was not found"));
    }

    private Event findEventByIdAndInitiatorIdIfExists(Long eventId, Long userId) {
        return eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventIsNotInRepositoryException("Event with id: " + eventId + " and initiatorId: "
                        + userId + " was not found"));
    }
}
