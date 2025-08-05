package ru.practicum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StatsDto {
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime timestamp;
}
