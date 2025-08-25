package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.event.state.EventStateActionSolution;
import ru.practicum.location.dto.LocationDto;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000, message = "Поле Аннотация должно быть в границах от {min} до {max} символов")
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000, message = "Поле Описание должно быть в границах от {min} до {max} символов")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    private Integer participantLimit;

    private Boolean requestModeration;

    private EventStateActionSolution stateAction;

    @Size(min = 3, max = 120, message = "Поле Заголовок должно быть в границах от {min} до {max} символов")
    private String title;
}
