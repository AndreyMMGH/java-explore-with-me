package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.StatsRequestDto;
import ru.practicum.StatsResponseDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.StatsMapper;
import ru.practicum.repository.StatsRepository;
import ru.practicum.view.StatsView;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Override
    public StatsRequestDto createHit(StatsRequestDto statsRequestDto) {
        return StatsMapper.toStatsDto(statsRepository.save(StatsMapper.toStats(statsRequestDto)));
    }

    @Override
    public List<StatsResponseDto> getStats(LocalDateTime start,
                                           LocalDateTime end,
                                           List<String> uris,
                                           Boolean unique) {
        if (start.isAfter(end)) {
            log.warn("Дата и время начала диапазона {} не может быть после даты и времени конца {}", start, end);
            throw new ValidationException("Дата и время начала диапазона " + start + " не может быть после даты и времени конца " + end);
        }

        List<StatsView> statsViews;

        if (uris == null || uris.isEmpty()) {
            statsViews = unique ? statsRepository.findUniqueStats(start, end) : statsRepository.findAllStats(start, end);
        } else {
            statsViews = unique ? statsRepository.findUniqueStatsWithUris(start, end, uris) : statsRepository.findAllStatsWithUris(start, end, uris);
        }

        return statsViews.stream()
                .map(sv -> new StatsResponseDto(sv.getApp(), sv.getUri(), sv.getHits()))
                .toList();
    }
}
