package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.StatsDto;
import ru.practicum.model.Stats;

@UtilityClass
public class StatsMapper {
    public static StatsDto toStatsDto(Stats stats) {
        return new StatsDto(
                stats.getApp(),
                stats.getUri(),
                stats.getIp(),
                stats.getTimestamp()
        );
    }

    public static Stats toStats(StatsDto statsDto) {
        return new Stats(
                null,
                statsDto.getApp(),
                statsDto.getUri(),
                statsDto.getIp(),
                statsDto.getTimestamp()
        );
    }
}
