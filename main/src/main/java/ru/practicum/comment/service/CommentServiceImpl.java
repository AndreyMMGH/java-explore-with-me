package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentMapper;
import ru.practicum.comment.dto.CommentRequestDto;
import ru.practicum.comment.dto.CommentResponseDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.state.EventState;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CommentResponseDto createComment(Long userId, Long eventId, CommentRequestDto commentRequestDto) {
        User user = findUser(userId);
        Event event = findEvent(eventId);

        return CommentMapper.toCommentResponseDto(commentRepository.save(CommentMapper.toComment(commentRequestDto, user, event)));
    }

    @Transactional
    @Override
    public CommentResponseDto updateComment(Long userId, Long commentId, CommentRequestDto commentRequestDto) {
        User user = findUser(userId);
        Comment comment = findComment(commentId);

        validateAuthorComment(user, comment);

        comment.setText(commentRequestDto.getText());
        comment.setUpdated(LocalDateTime.now());
        comment.setIsEdited(true);

        return CommentMapper.toCommentResponseDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentResponseDto> getUserComments(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());

        findUser(userId);

        return commentRepository.findByAuthorId(userId, pageable)
                .stream()
                .map(CommentMapper::toCommentResponseDto)
                .toList();
    }

    @Transactional
    @Override
    public void deleteComment(Long userId, Long commentId) {
        User user = findUser(userId);
        Comment comment = findComment(commentId);

        validateAuthorComment(user, comment);

        commentRepository.deleteById(commentId);
    }

    @Override
    public List<CommentResponseDto> getCommentsByEvent(Long eventId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());

        Event event = findEvent(eventId);

        validateEvents(event);

        return commentRepository.findByEventIdOrderByCreatedAsc(eventId, pageable)
                .stream()
                .map(CommentMapper::toCommentResponseDto)
                .toList();
    }

    @Override
    public CommentResponseDto getCommentById(Long eventId, Long commentId) {
        Event event = findEvent(eventId);
        Comment comment = findComment(commentId);

        validateEvents(event);

        if (!comment.getEvent().getId().equals(eventId)) {
            log.warn("Комментарий не относится к событию с id: {}", eventId);
            throw new ConflictException("Комментарий не относится к событию с id:" + eventId);
        }

        return CommentMapper.toCommentResponseDto(comment);
    }

    @Transactional
    @Override
    public CommentResponseDto updateCommentByAdmin(Long commentId, CommentRequestDto commentRequestDto) {
        Comment comment = findComment(commentId);

        comment.setText(commentRequestDto.getText());
        comment.setUpdated(LocalDateTime.now());
        comment.setIsEdited(true);

        return CommentMapper.toCommentResponseDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentResponseDto> searchComments(String text, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("created").descending());
        return commentRepository.findByTextContainingIgnoreCase(text, pageable)
                .stream()
                .map(CommentMapper::toCommentResponseDto)
                .toList();
    }

    @Transactional
    @Override
    public void deleteCommentByAdmin(Long commentId) {
        findComment(commentId);

        commentRepository.deleteById(commentId);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден"));
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id:" + eventId + " не найдено"));
    }

    private Comment findComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id:" + commentId + " не найден"));
    }

    private void validateAuthorComment(User user, Comment comment) {
        if (!comment.getAuthor().equals(user)) {
            log.warn("Автором комментария является другой пользователь");
            throw new ConflictException("Автором комментария является другой пользователь");
        }
    }

    private void validateEvents(Event event) {
        if (!EventState.PUBLISHED.equals(event.getState())) {
            log.warn("Событие не опубликовано. Получить комментарий невозможно");
            throw new NotFoundException("Событие не опубликовано. Получить комментарий невозможно");
        }
    }
}
