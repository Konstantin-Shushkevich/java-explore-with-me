package ru.practicum.service.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.model.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE e.initiator.id = :userId ORDER BY e.id ASC")
    Page<Event> findByInitiatorIdWithDetails(@Param("userId") Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long id, Long initiatorId);

    boolean existsByIdAndInitiatorId(Long id, Long initiatorId);

    Optional<Event> findByIdAndState(Long id, EventState state);

    @Query("select e " +
            "from Event as e " +
            "where (:users is null or e.initiator.id in :users) " +
            "and (:states is null or e.state in :states) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and ((e.eventDate >= :rangeStart) or (cast(:rangeStart as timestamp) is null)) " +
            "and ((e.eventDate <= :rangeEnd) or (cast(:rangeEnd as timestamp) is null))")
    Page<Event> getEventsForAdmin(List<Long> users, List<EventState> states, List<Long> categories,
                                  LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable pageable);

    @Query("select e " +
            "from Event as e " +
            "where (:text is null or ((lower(e.description) like lower(concat('%', :text, '%'))) or " +
            "(lower(e.annotation) like lower(concat('%', :text, '%'))))) " +
            "and (:categories is null or e.category.id in :categories) " +
            "and (:paid is null or e.paid = :paid) " +
            "and (e.eventDate >= :rangeStart) " +
            "and (cast(:rangeEnd as timestamp ) is null or e.eventDate <= :rangeEnd) " +
            "and (:onlyAvailable = false or " +
            "(:onlyAvailable = true and " +
            "(e.participantLimit = 0 or " +
            "(e.participantLimit - (select count(r) from ParticipationRequest as r where r.event.id = e.id " +
            "and r.status = 'CONFIRMED') > 0)))) " +
            "and e.state = 'PUBLISHED'")
    Page<Event> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                          LocalDateTime rangeEnd, Boolean onlyAvailable, Pageable pageable);

    Set<Event> findByIdIn(Set<Long> events);
}
