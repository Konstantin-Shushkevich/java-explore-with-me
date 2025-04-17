package ru.practicum.service.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.service.request.model.ParticipationRequest;
import ru.practicum.service.request.model.ParticipationRequestStatus;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findByRequesterId(Long requesterId);

    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    Integer countAllByEventIdAndStatus(Long eventId, ParticipationRequestStatus status);

    List<ParticipationRequest> findByEventId(Long eventId);

    List<ParticipationRequest> findByEventIdAndStatusAndIdIn(Long eventId, ParticipationRequestStatus status,
                                                             List<Long> requestIds);
}
