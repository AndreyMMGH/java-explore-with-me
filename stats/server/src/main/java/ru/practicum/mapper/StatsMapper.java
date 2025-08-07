package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.StatsRequestDto;
import ru.practicum.model.Stats;

@UtilityClass
public class StatsMapper {
    public static StatsRequestDto toStatsDto(Stats stats) {
        return new StatsRequestDto(
                stats.getApp(),
                stats.getUri(),
                stats.getIp(),
                stats.getTimestamp()
        );
    }

    public static Stats toStats(StatsRequestDto statsRequestDto) {
        return new Stats(
                null,
                statsRequestDto.getApp(),
                statsRequestDto.getUri(),
                statsRequestDto.getIp(),
                statsRequestDto.getTimestamp()
        );
    }
}
