package ru.practicum.comment.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {
    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getText(),
                comment.getEvent().getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getName(),
                comment.getCreated(),
                comment.getUpdated(),
                comment.getIsEdited()
        );
    }

    public static Comment toComment(CommentRequestDto commentRequestDto, User user, Event event) {
        return new Comment(
                null,
                commentRequestDto.getText(),
                event,
                user,
                LocalDateTime.now(),
                null,
                false
        );
    }
}