package ru.practicum.server.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;
import ru.practicum.server.service.HitService;

import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.server.util.Constants.DATE_TIME_FORMAT;

@Slf4j
@RestController
@RequiredArgsConstructor
public class HitController {

    private final HitService hitService;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitDto saveHit(@Valid @RequestBody HitDto hitDto) {
        log.trace("Saving statistics hit: {}", hitDto);
        return hitService.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> findStats(@RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime start,
                                    @RequestParam @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime end,
                                    @RequestParam(required = false) List<String> uris,
                                    @RequestParam(required = false, defaultValue = "false") boolean unique) {
        log.trace("Getting statistics s started (at server level of stats module)");
        return hitService.findStats(start, end, uris, unique);
    }
}
