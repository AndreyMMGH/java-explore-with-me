package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.location.LocationDto;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NewEventDto {
    @NotBlank(message = "Аннотация не может быть пустой")
    @Size(min = 20, max = 2000, message = "Поле Аннотация должно быть в границах от {min} до {max} символов")
    private String annotation;

    @NotNull(message = "Категория обязательна")
    private Long category;

    @NotBlank(message = "Описание не может быть пустым")
    @Size(min = 20, max = 7000, message = "Поле Описание должно быть в границах от {min} до {max} символов")
    private String description;

    @NotNull(message = "Дата события обязательна")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @NotNull(message = "Локация обязательна")
    private LocationDto location;

    private Boolean paid = false;

    private Integer participantLimit = 0;

    private Boolean requestModeration = true;

    @NotBlank(message = "Заголовок не может быть пустым")
    @Size(min = 3, max = 120, message = "Поле Заголовок должно быть в границах от {min} до {max} символов")
    private String title;
}
