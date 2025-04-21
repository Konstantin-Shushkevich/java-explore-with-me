package ru.practicum.service.event.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatClient;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.service.event.model.Event;
import ru.practicum.service.event.model.EventState;
import ru.practicum.service.exception.StatisticServerException;
import ru.practicum.service.request.model.ParticipationRequestStatus;
import ru.practicum.service.request.repository.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventStatisticsService {
    private final ParticipationRequestRepository participationRequestRepository;
    private final StatClient statClient;

    public Integer getConfirmedRequests(Event event) {
        return event.getState().equals(EventState.PUBLISHED) ?
                participationRequestRepository.countAllByEventIdAndStatus(
                        event.getId(),
                        ParticipationRequestStatus.CONFIRMED) : 0;
    }

    public Long getViews(Event event) {

        if (!event.getState().equals(EventState.PUBLISHED)) {
            return 0L;
        }

        List<StatsDto> statsDto;

        statsDto = statClient.readStats(
                event.getPublishedOn(),
                LocalDateTime.now(),
                List.of(String.format("/events/%d", event.getId())),
                true
        );

        return statsDto.isEmpty() ? 0L : statsDto.getFirst().getHits();
    }

    public void saveHit(HttpServletRequest request) {
        ResponseEntity<Object> response = statClient.addHit(
                HitDto.builder()
                        .app("main-service")
                        .uri(request.getRequestURI())
                        .ip(request.getRemoteAddr())
                        .timestamp(LocalDateTime.now())
                        .build());

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new StatisticServerException("Error at the statistics-client");
        }
    }
}
