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
@RequestMapping(path = "/admin/comments")
public class CommentAdminController {
    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public CommentResponseDto updateCommentByAdmin(
            @PathVariable Long commentId,
            @RequestBody @Valid CommentRequestDto commentRequestDto) {
        log.info("PATCH /admin/comments/{}", commentId);
        return commentService.updateCommentByAdmin(commentId, commentRequestDto);
    }

    @GetMapping("/search")
    public List<CommentResponseDto> searchComments(@RequestParam String text,
                                                   @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                   @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("GET /admin/comments/{}", text);
        return commentService.searchComments(text, from, size);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}")
    public void deleteCommentByAdmin(@PathVariable Long commentId) {
        log.info("DELETE /admin/comments/{}", commentId);
        commentService.deleteCommentByAdmin(commentId);
    }
}
