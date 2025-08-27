package ru.practicum.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CommentRequestDto {
    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(min = 1, max = 2000, message = "Комментарий должен быть длинной от {min} до {max} символов")
    private String text;
}
