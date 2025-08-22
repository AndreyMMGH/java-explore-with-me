package ru.practicum.event.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.request.status.RequestStatus;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class EventRequestStatusUpdateRequest {
    @NotNull(message = "Идентификаторы запросов обязательны")
    List<Long> requestIds;
    @NotNull(message = "Статус обязателен")
    RequestStatus status;
}
