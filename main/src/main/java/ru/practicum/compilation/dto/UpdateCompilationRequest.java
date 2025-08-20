package ru.practicum.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UpdateCompilationRequest {
    private Set<Long> events;

    private Boolean pinned;

    @Size(min = 1, max = 50, message = "Заголовок должен быть в границах от {min} до {max} символов")
    private String title;
}
