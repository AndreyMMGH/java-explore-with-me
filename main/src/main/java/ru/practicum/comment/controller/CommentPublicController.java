package ru.practicum.comment.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.service.CommentService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/events/{eventId}/comments")
public class CommentPublicController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentResponseDto> getCommentsByEvent(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("GET /comments?eventId={}&from={}&size={}", eventId, from, size);
        return commentService.getCommentsByEvent(eventId, from, size);
    }

    @GetMapping("/{commentId}")
    public CommentResponseDto getCommentById(@PathVariable Long eventId,
                                             @PathVariable Long commentId) {
        log.info("GET /events/{}/comments/{}", eventId, commentId);
        return commentService.getCommentById(eventId, commentId);
    }
}
