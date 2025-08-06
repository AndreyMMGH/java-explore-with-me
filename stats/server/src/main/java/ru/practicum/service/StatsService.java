package ru.practicum.service;

import ru.practicum.StatsDto;
import ru.practicum.StatsResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    StatsDto createHit(StatsDto statsDto);

    List<StatsResponseDto> getStats(LocalDateTime start,
                                    LocalDateTime end,
                                    List<String> uris,
                                    Boolean unique);
}
