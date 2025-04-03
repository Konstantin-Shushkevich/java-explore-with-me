package ru.practicum.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.server.exception.DateTimeNotValidException;
import ru.practicum.server.repository.HitRepository;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.server.mapper.HitMapper.toHit;
import static ru.practicum.server.mapper.HitMapper.toHitDto;

@Service
@RequiredArgsConstructor
public class HitServiceImpl implements HitService {

    private final HitRepository hitRepository;

    @Override
    public HitDto saveHit(HitDto hitDto) {
        return toHitDto(hitRepository.save(toHit(hitDto)));
    }

    @Override
    public List<StatsDto> findStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            throw new DateTimeNotValidException("Start cannot be after end");
        }

        if (unique) {
            return hitRepository.getUniqueStats(start, end, uris);
        } else {
            return hitRepository.getStats(start, end, uris);
        }

    }
}
