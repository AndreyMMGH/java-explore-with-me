package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.StatsDto;
import ru.practicum.service.StatsService;

@Slf4j
@RequiredArgsConstructor
@RestController
public class StatsController {
    private final StatsService statsService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/hit")
    public StatsDto createHit (@RequestBody StatsDto statsDto) {
        return statsService.createHit(statsDto);
    }
}
