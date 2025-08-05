package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.StatsDto;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.repository.StatsRepository;

@RequiredArgsConstructor
@Service
public class StatsServiceImpl {
    private final StatsRepository statsRepository;

    public StatsDto createHit(StatsDto statsDto) {
        return StatsMapper.toStatsDto(statsRepository.save(StatsMapper.toStats(statsDto)));
    }
}
