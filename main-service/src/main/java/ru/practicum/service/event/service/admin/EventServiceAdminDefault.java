package ru.practicum.service.event.service.admin;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.service.event.dto.UpdateEventAdminRequest;
import ru.practicum.service.event.dto.mapper.EventMapper;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.model.EventState;
import ru.practicum.service.event.model.Location;
import ru.practicum.service.event.repository.EventRepository;
import ru.practicum.service.exception.CategoryIsNotInRepositoryException;
import ru.practicum.service.exception.EventIsNotInRepositoryException;
import ru.practicum.service.exception.StatisticServerException;
import ru.practicum.service.exception.ViolationOfTermsException;
import ru.practicum.service.request.model.ParticipationRequestStatus;
import ru.practicum.service.request.repository.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceAdminDefault implements EventServiceAdmin {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatClient statClient;

    @Override
    public List<EventFullDto> findAllByCriteria(List<Long> users,
                                                List<EventState> states,
                                                List<Long> categories,
                                                LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd,
                                                int from,
                                                int size) {
        return eventRepository.getEventsForAdmin(users, states, categories, rangeStart, rangeEnd,
                        getPageable(from, size, Sort.by("id").ascending()))
                .stream().map(event -> EventMapper.toEventFullDto(event, getConfirmedRequests(event), getViews(event)))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateById(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event eventFromBd = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventIsNotInRepositoryException("Event with id: " + eventId + " was not found"));

        if (!Objects.isNull(updateEventAdminRequest.getStateAction())
                && !eventFromBd.getState().equals(EventState.PENDING)) {
            throw new ViolationOfTermsException("Event with id: " + eventId + " does not have the PENDING status");
        }

        Category category = eventFromBd.getCategory();
        if (!Objects.isNull(updateEventAdminRequest.getCategory())) {
            category = categoryRepository.findById(updateEventAdminRequest.getCategory())
                    .orElseThrow(() -> new CategoryIsNotInRepositoryException("Category with id: "
                            + updateEventAdminRequest.getCategory() + " was not found"));
        }

        Location location = Objects.isNull(updateEventAdminRequest.getLocation()) ?
                eventFromBd.getLocation() : updateEventAdminRequest.getLocation();
        Event newEvent = EventMapper.toEvent(updateEventAdminRequest, eventFromBd, category, location);

        if (!Objects.isNull(newEvent.getPublishedOn())
                && newEvent.getEventDate().isBefore(newEvent.getPublishedOn().plusHours(1))) {
            throw new ViolationOfTermsException("The date of the event must be no earlier than one hour " +
                    "from the date of publication");
        }

        return EventMapper.toEventFullDto(eventRepository
                .save(newEvent), getConfirmedRequests(newEvent), getViews(newEvent));
    }

    private Pageable getPageable(int from, int size, Sort sort) {
        int page = from / size;
        return Objects.isNull(sort) ? PageRequest.of(page, size) : PageRequest.of(page, size, sort);
    }

    private Integer getConfirmedRequests(Event event) {
        return event.getState().equals(EventState.PUBLISHED) ?
                participationRequestRepository.countAllByEventIdAndStatus(event.getId(), ParticipationRequestStatus.CONFIRMED) : 0;
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

    public Set<EventShortDto> toEventShortDtoList(Set<Event> events) {
        return events.stream().map(event -> EventMapper.toEventShortDto(event, getConfirmedRequests(event),
                getViews(event))).collect(Collectors.toSet());
    }
}
