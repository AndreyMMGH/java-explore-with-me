package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users/{userId}/comments")
public class CommentPrivateController {
    private final CommentService commentService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CommentResponseDto createComment(@PathVariable Long userId,
                                            @RequestParam Long eventId,
                                            @RequestBody @Valid CommentRequestDto commentRequestDto) {
        log.info("Запрос на комментарий в событии с id= {}. POST /users/{}/comments", eventId, userId);
        return commentService.createComment(userId, eventId, commentRequestDto);
    }

    @PatchMapping("/{commentId}")
    public CommentResponseDto updateComment(@PathVariable Long userId,
                                            @PathVariable Long commentId,
                                            @RequestBody @Valid CommentRequestDto commentRequestDto) {
        log.info("PATCH /users/{}/comments/{}", userId, commentId);
        return commentService.updateComment(userId, commentId, commentRequestDto);
    }

    @GetMapping
    public List<CommentResponseDto> getUserComments(@PathVariable Long userId,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                    @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("GET /users/{}/comments?from={}&size={}", userId, from, size);
        return commentService.getUserComments(userId, from, size);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable Long userId, @PathVariable Long commentId) {
        commentService.deleteComment(userId, commentId);
    }
}
