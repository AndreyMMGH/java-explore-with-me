package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatsRequestDto;
import ru.practicum.StatsResponseDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class StatsController {
    private final StatsService statsService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/hit")
    public StatsRequestDto createHit(@RequestBody StatsRequestDto statsRequestDto) {
        log.info("POST /hit");
        return statsService.createHit(statsRequestDto);
    }

    @GetMapping(value = "/stats")
    public List<StatsResponseDto> getStats(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Valid @NonNull LocalDateTime start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") @Valid @NonNull LocalDateTime end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") Boolean unique) {
        log.info("GET /stats");
        return statsService.getStats(start, end, uris, unique);
    }
}
