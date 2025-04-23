package ru.practicum.service.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.model.EventState;
import ru.practicum.service.event.repository.EventRepository;
import ru.practicum.service.exception.EventIsNotInRepositoryException;
import ru.practicum.service.exception.RequestNotFoundException;
import ru.practicum.service.exception.UserIsNotInRepositoryException;
import ru.practicum.service.exception.ViolationOfTermsException;
import ru.practicum.service.request.dto.ParticipationRequestDto;
import ru.practicum.service.request.dto.mapper.ParticipationRequestMapper;
import ru.practicum.service.request.model.ParticipationRequest;
import ru.practicum.service.request.model.ParticipationRequestStatus;
import ru.practicum.service.request.repository.ParticipationRequestRepository;
import ru.practicum.service.user.model.User;
import ru.practicum.service.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ParticipationRequestServiceDefault implements ParticipationRequestService {

    private final ParticipationRequestRepository participationRequestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> findAllForUserByFilters(Long userId) {
        findUserIfExists(userId);

        return participationRequestRepository.findByRequesterId(userId).stream()
                .map(ParticipationRequestMapper::toParticipationRequestDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto add(Long userId, Long eventId) {
        User user = findUserIfExists(userId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventIsNotInRepositoryException("Event with id: " + eventId + " was not found"));

        validateRequestCreation(userId, event);

        ParticipationRequest participationRequest = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(user)
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            participationRequest.setStatus(ParticipationRequestStatus.CONFIRMED);
        } else {
            participationRequest.setStatus(ParticipationRequestStatus.PENDING);
        }

        return ParticipationRequestMapper
                .toParticipationRequestDto(participationRequestRepository.save(participationRequest));
    }

    @Override
    @Transactional
    public ParticipationRequestDto updateStatus(Long userId, Long requestId) {
        findUserIfExists(userId);

        ParticipationRequest request = participationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException("Request with id=" + requestId + " was not found"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new ViolationOfTermsException("Only the requester can cancel the request");
        }

        request.setStatus(ParticipationRequestStatus.CANCELED);

        return ParticipationRequestMapper.toParticipationRequestDto(participationRequestRepository.save(request));
    }

    private User findUserIfExists(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserIsNotInRepositoryException("User with id: " + userId + " was not found"));
    }

    private void validateRequestCreation(Long userId, Event event) {
        if (event.getInitiator().getId().equals(userId)) {
            throw new ViolationOfTermsException(
                    "Initiator cannot participate in own event");
        }
        if (participationRequestRepository.existsByEventIdAndRequesterId(
                event.getId(), userId)) {
            throw new ViolationOfTermsException(
                    "Request already exists for user: " + userId + " and event: " + event.getId());
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new ViolationOfTermsException(
                    "Event: " + event.getId() + " is not published");
        }
        if (event.getParticipantLimit() != 0 && participationRequestRepository.countAllByEventIdAndStatus(event.getId(),
                ParticipationRequestStatus.CONFIRMED) >= event.getParticipantLimit()) {
            throw new ViolationOfTermsException("The limit of participation requests has been already reached");
        }
    }
}
