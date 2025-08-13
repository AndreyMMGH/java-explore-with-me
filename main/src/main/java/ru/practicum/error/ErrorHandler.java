package ru.practicum.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(NotFoundException e) {
        return new ApiError(
                null,
                e.getMessage(),
                "Не найдено",
                "NOT_FOUND",
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(ValidationException e) {
        return new ApiError(
                null,
                e.getMessage(),
                "Cервер не смог обработать запроc",
                "BAD_REQUEST",
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflict(ConflictException e) {
        return new ApiError(
                null,
                e.getMessage(),
                "Конфликт запроса",
                "CONFLICT",
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(Throwable e) {
        return new ApiError(
                null,
                e.getMessage(),
                "Внутренняя ошибка сервера",
                "INTERNAL_SERVER_ERROR",
                LocalDateTime.now()
        );
    }
}
