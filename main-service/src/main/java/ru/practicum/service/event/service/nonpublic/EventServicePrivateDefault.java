package ru.practicum.service.event.service.nonpublic;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatClient;
import ru.practicum.dto.StatsDto;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.service.event.dto.mapper.EventMapper.*;

@Service
@RequiredArgsConstructor
public class EventServicePrivateDefault implements EventServicePrivate {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatClient statClient;

    @Override
    public List<EventShortDto> getByAuthor(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new UserIsNotInRepositoryException("User with id: " + userId + " is not in repository");
        }

        Page<Event> events = eventRepository.findByInitiatorIdWithDetails(userId, getPageable(
                from, size, Sort.by("id").ascending()));

        return events.map(event -> toEventShortDto(event, getConfirmedRequests(event), getViews(event))).getContent();
    }

    @Override
    public EventFullDto add(Long userId, NewEventDto newEventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserIsNotInRepositoryException("User with id: " + userId + " was not found"));

        Category category = categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new EventIsNotInRepositoryException("Category with id: " + newEventDto.getCategory()
                        + " was not found"));

        return toEventFullDto(eventRepository.save(toEvent(newEventDto, category, user)), 0, 0L);
    }

    @Override
    public EventFullDto findById(Long userId, Long eventId) {
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventIsNotInRepositoryException("Event with id: " + eventId +
                        "or user with id: " + userId + " was not found"));
        return EventMapper.toEventFullDto(event, getConfirmedRequests(event), getViews(event));
    }

    @Override
    public EventFullDto updateById(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        if (!userRepository.existsById(userId)) {
            throw new UserIsNotInRepositoryException("User with id: " + userId + " was not found");
        }

        Event eventFromBd = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventIsNotInRepositoryException("Event with id: " + eventId + " and initiatorId: "
                        + userId + " was not found"));
        if (eventFromBd.getState().equals(EventState.PUBLISHED)) {
            throw new ViolationOfTermsException("Only PENDING or CANCELED events can be changed");
        }
        Category category = eventFromBd.getCategory();
        if (!Objects.isNull(updateEventUserRequest.getCategory())) {
            category = categoryRepository.findById(updateEventUserRequest.getCategory())
                    .orElseThrow(() -> new CategoryIsNotInRepositoryException("Category with id: "
                            + updateEventUserRequest.getCategory() + " was not found"));
        }
        Location location = Objects.isNull(updateEventUserRequest.getLocation()) ?
                eventFromBd.getLocation() : updateEventUserRequest.getLocation();
        Event newEvent = EventMapper.toEvent(updateEventUserRequest, eventFromBd, category, location);
        return EventMapper.toEventFullDto(eventRepository.save(newEvent),
                getConfirmedRequests(newEvent),
                getViews(newEvent));
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new UserIsNotInRepositoryException("User with id: " + userId + " was not found");
        }
        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId))
            throw new EventIsNotInRepositoryException("Event with id: " + eventId + " and userId:" + userId +
                    " was not found");
        return participationRequestRepository.findByEventId(eventId).stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {

        if (!userRepository.existsById(userId)) {
            throw new UserIsNotInRepositoryException("User with id: " + userId + " was not found");
        }

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new EventIsNotInRepositoryException("Event with id: " + eventId + " and userId: "
                        + userId + " was not found"));

        if (!event.getRequestModeration()) {
            throw new ViolationOfTermsException("For an event with id: " + eventId
                    + " confirmation of requests  is not required");
        }

        int available = event.getParticipantLimit() - getConfirmedRequests(event);

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
            throw new StatisticServerException("Error at the statistics-client");
        }
        return statsDto.isEmpty() ? 0L : statsDto.getFirst().getHits();
    }
}
