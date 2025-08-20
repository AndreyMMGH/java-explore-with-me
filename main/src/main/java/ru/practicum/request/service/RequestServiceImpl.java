package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.state.EventState;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.request.status.RequestStatus;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User user = findUser(userId);
        Event event = findEvent(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может добавить заявку на участие в своём событии");
        }

        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }

        boolean exists = requestRepository.existsByRequesterIdAndEventId(userId, eventId);
        if (exists) {
            throw new ConflictException("Нельзя добавить повторный запрос на участие в событии");
        }

        long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() != null && event.getParticipantLimit() > 0 &&
                confirmedRequests >= event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит запросов на участие в событии");
        }

        Request request = new Request();
        request.setEvent(event);
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());

        if (Boolean.FALSE.equals(event.getRequestModeration()) || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByUserId(Long userId) {
        findUser(userId);

        List<Request> requests = requestRepository.findAllByRequesterId(userId);

        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        findUser(userId);

        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id:" + requestId + " не найден"));

        if (!request.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Заявка с id:" + requestId + " недоступна для пользователя c id:" + userId);
        }

        request.setStatus(RequestStatus.CANCELED);

        return RequestMapper.toParticipationRequestDto(requestRepository.save(request));
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id:" + id + " не найден"));
    }

    private Event findEvent(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id:" + id + " не найдено"));
    }
}
