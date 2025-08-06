package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.StatsView;
import ru.practicum.model.Stats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Stats, Long> {
    @Query("SELECT s.app AS app, s.uri AS uri, COUNT(DISTINCT s.ip) AS hits " +
            "FROM Stats AS s " +
            "WHERE s.timestamp BETWEEN :start and :end " +
            "GROUP BY app, uri " +
            "ORDER BY hits DESC")
    List<StatsView> findUniqueStats(LocalDateTime start, LocalDateTime end);

    @Query("SELECT s.app AS app, s.uri AS uri, COUNT(s.ip) AS hits " +
            "FROM Stats AS s " +
            "WHERE s.timestamp BETWEEN :start and :end " +
            "GROUP BY app, uri " +
            "ORDER BY hits DESC")
    List<StatsView> findAllStats(LocalDateTime start, LocalDateTime end);

    @Query("SELECT s.app AS app, s.uri AS uri, COUNT(DISTINCT s.ip) AS hits " +
            "FROM Stats AS s " +
            "WHERE s.timestamp BETWEEN :start and :end " +
            "AND uri IN :uris " +
            "GROUP BY app, uri " +
            "ORDER BY hits DESC")
    List<StatsView> findUniqueStatsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT s.app AS app, s.uri AS uri, COUNT(s.ip) AS hits " +
            "FROM Stats AS s " +
            "WHERE s.timestamp BETWEEN :start and :end " +
            "AND uri IN :uris " +
            "GROUP BY app, uri " +
            "ORDER BY hits DESC")
    List<StatsView> findAllStatsWithUris(LocalDateTime start, LocalDateTime end, List<String> uris);
}
