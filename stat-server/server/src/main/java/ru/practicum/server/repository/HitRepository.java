package ru.practicum.server.repository;

import org.springframework.data.jpa.repository.Query;
import ru.practicum.dto.StatsDto;
import ru.practicum.server.model.Hit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository extends JpaRepository<Hit, Long> {

    @Query("select new ru.practicum.dto.StatsDto(h.app, h.uri, count(h.ip)) " +
            "from Hit as h " +
            "where h.timestamp between :start and :end " +
            "and ((:uris) is null or h.uri in (:uris)) " +
            "group by h.app, h.uri " +
            "order by count(h.ip) desc")
    List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("select new ru.practicum.dto.StatsDto(h.app, h.uri, count(distinct h.ip)) " +
            "from Hit as h " +
            "where (h.timestamp between :start and :end) " +
            "and ((:uris) is null  or h.uri in (:uris)) " +
            "group by h.app, h.uri " +
            "order by count(distinct h.ip) desc")
    List<StatsDto> getUniqueStats(LocalDateTime start, LocalDateTime end, List<String> uris);
}
