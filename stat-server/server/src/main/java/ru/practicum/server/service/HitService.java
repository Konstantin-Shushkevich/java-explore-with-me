package ru.practicum.server.service;

import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {
    HitDto saveHit(HitDto hitDto);

    List<StatsDto> findStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    List<StatsDto> findStatsIfUnique(LocalDateTime start, LocalDateTime end, List<String> uris);
}
