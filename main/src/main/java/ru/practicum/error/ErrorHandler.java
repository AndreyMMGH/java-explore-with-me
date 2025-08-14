package ru.practicum.error;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(NotFoundException e) {
        return new ApiError(
                Collections.emptyList(),
                e.getMessage(),
                "Not found.",
                "NOT_FOUND",
                LocalDateTime.now()
        );
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> String.format("Field: %s. Error: %s. Value: %s",
                        fe.getField(),
                        fe.getDefaultMessage(),
                        fe.getRejectedValue() != null ? fe.getRejectedValue().toString() : "null"))
                .collect(Collectors.joining("; "));

        return new ApiError(
                Collections.emptyList(),
                message,
                "Incorrectly made request.",
                "BAD_REQUEST",
                LocalDateTime.now()
        );
    }

    @ExceptionHandler({ConflictException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(ConflictException e) {
        return new ApiError(
                Collections.emptyList(),
                e.getMessage(),
                "Integrity constraint has been violated.",
                "CONFLICT",
                LocalDateTime.now()
        );
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConstraintViolationException(ConstraintViolationException e) {
        return new ApiError(
                Collections.emptyList(),
                "Категория с таким названием уже существует.",
                "Integrity constraint has been violated.",
                "CONFLICT",
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(Throwable e) {
        return new ApiError(
                Collections.emptyList(),
                "Произошла непредвиденная ошибка.",
                "Internal server error.",
                "INTERNAL_SERVER_ERROR",
                LocalDateTime.now()
        );
    }
}
