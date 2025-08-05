package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.Stats;

public interface StatsRepository extends JpaRepository<Stats, Long> {

}
