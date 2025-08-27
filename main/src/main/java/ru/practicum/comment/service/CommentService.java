package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;

import java.util.List;

public interface CommentService {
    CommentResponseDto createComment(Long userId, Long eventId, CommentRequestDto commentRequestDto);

    CommentResponseDto updateComment(Long userId, Long commentId, CommentRequestDto commentRequestDto);

    List<CommentResponseDto> getUserComments(Long userId, Integer from, Integer size);

    void deleteComment(Long userId, Long commentId);

    List<CommentResponseDto> getCommentsByEvent(Long eventId, Integer from, Integer size);

    CommentResponseDto getCommentById(Long eventId, Long commentId);

    CommentResponseDto updateCommentByAdmin(Long commentId, CommentRequestDto commentRequestDto);

    List<CommentResponseDto> searchComments(String text, Integer from, Integer size);

    void deleteCommentByAdmin(Long commentId);
}
