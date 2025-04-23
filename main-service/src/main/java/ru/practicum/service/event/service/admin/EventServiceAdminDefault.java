package ru.practicum.service.event.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.category.model.Category;
import ru.practicum.service.category.repository.CategoryRepository;
import ru.practicum.service.event.dto.EventFullDto;
import ru.practicum.service.event.dto.EventShortDto;
import ru.practicum.service.event.dto.UpdateEventAdminRequest;
import ru.practicum.service.event.dto.mapper.EventMapper;
import ru.practicum.service.event.dto.params.EventAdminFilterParams;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.model.EventState;
import ru.practicum.service.event.model.Location;
import ru.practicum.service.event.repository.EventRepository;
import ru.practicum.service.event.service.EventStatisticsService;
import ru.practicum.service.exception.CategoryIsNotInRepositoryException;
import ru.practicum.service.exception.EventIsNotInRepositoryException;
import ru.practicum.service.exception.ViolationOfTermsException;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.service.util.pageable.PageableUtils.getPageable;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceAdminDefault implements EventServiceAdmin {

    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final EventStatisticsService eventStatisticsService;

    @Override
    public List<EventFullDto> findAllByCriteria(EventAdminFilterParams eventAdminFilterParams) {

        Page<Event> events = eventRepository.getEventsForAdmin(eventAdminFilterParams.getUsers(),
                eventAdminFilterParams.getStates(),
                eventAdminFilterParams.getCategories(),
                eventAdminFilterParams.getRangeStart(),
                eventAdminFilterParams.getRangeEnd(),
                getPageable(
                        eventAdminFilterParams.getFrom(), eventAdminFilterParams.getSize(),
                        Sort.by("id").ascending()));

        return events.stream()
                .map(event -> EventMapper.toEventFullDto(event, eventStatisticsService.getConfirmedRequests(event),
                        eventStatisticsService.getViews(event)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
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

        return EventMapper.toEventFullDto(
                eventRepository.save(newEvent), eventStatisticsService.getConfirmedRequests(newEvent),
                eventStatisticsService.getViews(newEvent));
    }

    public Set<EventShortDto> toEventShortDtoList(Set<Event> events) {
        return events.stream().map(event -> EventMapper.toEventShortDto(
                event,
                eventStatisticsService.getConfirmedRequests(event),
                eventStatisticsService.getViews(event))).collect(Collectors.toSet());
    }
}
