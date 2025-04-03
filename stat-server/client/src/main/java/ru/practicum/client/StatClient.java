package ru.practicum.client;

import org.springframework.http.ResponseEntity;
import ru.practicum.dto.HitDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatClient {
    ResponseEntity<Object> addHit(HitDto hitDto);

    ResponseEntity<Object> readStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
