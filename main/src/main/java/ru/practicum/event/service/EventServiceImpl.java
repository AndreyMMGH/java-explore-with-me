package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.dto.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.state.EventState;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.Location;
import ru.practicum.location.LocationRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.request.status.RequestStatus;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Slf4j
@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;

    @Transactional
    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        User initiator = findUser(userId);
        Category category = findCategory(newEventDto.getCategory());
        validateEventDate(newEventDto.getEventDate());

        Event event = EventMapper.toEvent(newEventDto, category, initiator);

        if (event.getLocation() != null) {
            Location savedLocation = locationRepository.save(event.getLocation());
            event.setLocation(savedLocation);
        }

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size) {
        findUser(userId);

        Pageable pageable = PageRequest.of(from / size, size, Sort.by("eventDate").ascending());

        return eventRepository.findByInitiatorId(userId, pageable)
                .stream()
                .map(EventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId) {
        findUser(userId);
        Event event = findEvent(userId, eventId);

        return EventMapper.toEventFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto updateEventByUserIdAndEventId(Long userId,
                                                      Long eventId,
                                                      UpdateEventUserRequest updateEventUserRequest) {
        findUser(userId);
        Event event = findEvent(userId, eventId);

        if (!(event.getState().equals(EventState.PENDING) || event.getState().equals(EventState.CANCELED))) {
            log.warn("Изменять можно только события в состоянии ожидания модерации или отмененные. Текущее состояние: {}", event.getState());
            throw new ConflictException("Изменять можно только события в состоянии ожидания модерации или отмененные");
        }

        if (updateEventUserRequest.getEventDate() != null) {
            validateEventDate(updateEventUserRequest.getEventDate());
            event.setEventDate(updateEventUserRequest.getEventDate());
        }

        if (updateEventUserRequest.getAnnotation() != null) {
            event.setAnnotation(updateEventUserRequest.getAnnotation());
        }
        if (updateEventUserRequest.getCategory() != null) {
            Category category = findCategory(updateEventUserRequest.getCategory());
            event.setCategory(category);
        }
        if (updateEventUserRequest.getDescription() != null) {
            event.setDescription(updateEventUserRequest.getDescription());
        }
        if (updateEventUserRequest.getPaid() != null) {
            event.setPaid(updateEventUserRequest.getPaid());
        }
        if (updateEventUserRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventUserRequest.getParticipantLimit());
        }
        if (updateEventUserRequest.getTitle() != null) {
            event.setTitle(updateEventUserRequest.getTitle());
        }

        if (updateEventUserRequest.getStateAction() != null) {
            switch (updateEventUserRequest.getStateAction()) {
                case SEND_TO_REVIEW -> event.setState(EventState.PENDING);
                case CANCEL_REVIEW -> event.setState(EventState.CANCELED);
                default -> throw new ConflictException("Недопустимое состояние события для обновления");
            }
        }

        return EventMapper.toEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<ParticipationRequestDto> getEventRequestsByUserIdAndEventId(Long userId, Long eventId) {
        findEvent(userId, eventId);

        List<Request> requests = requestRepository.findAllByEventId(eventId);

        return requests.stream()
                .map(RequestMapper::toParticipationRequestDto)
                .toList();
    }

    @Override
    public EventRequestStatusUpdateResult changeStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        Event event = findEvent(userId, eventId);

        if (event.getParticipantLimit() == 0 || Boolean.FALSE.equals(event.getRequestModeration())) {
            List<Request> requests = requestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());
            List<ParticipationRequestDto> confirmed = requests.stream()
                    .map(RequestMapper::toParticipationRequestDto)
                    .toList();
            return new EventRequestStatusUpdateResult(confirmed, Collections.emptyList());
        }

        long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);

        List<Request> requestsToUpdate = requestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        for (Request r : requestsToUpdate) {
            if (!r.getStatus().equals(RequestStatus.PENDING)) {
                throw new ConflictException("Запрос должен иметь статус PENDING");
            }

            if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
                if (confirmedCount >= event.getParticipantLimit()) {
                    throw new ConflictException("Лимит участников достигнут");
                }
                r.setStatus(RequestStatus.CONFIRMED);
                confirmedCount++;
                confirmedRequests.add(RequestMapper.toParticipationRequestDto(r));
            } else if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatus.REJECTED)) {
                r.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(RequestMapper.toParticipationRequestDto(r));
            } else {
                throw new ConflictException("Неверный статус для обновления");
            }

            if (confirmedCount >= event.getParticipantLimit()) {
                List<Request> pendingRequests = requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.PENDING);
                for (Request pr : pendingRequests) {
                    pr.setStatus(RequestStatus.REJECTED);
                    rejectedRequests.add(RequestMapper.toParticipationRequestDto(pr));
                }
                break;
            }
        }
        requestRepository.saveAll(requestsToUpdate);

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }


    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь по id: " + id + " не найден"));
    }

    private Category findCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория по id: " + id + " не найдена"));
    }

    private Event findEvent(Long userId, Long eventId) {
        return eventRepository.findByInitiatorIdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException("Событие текущего пользователя не найдено"));
    }

    private void validateEventDate(LocalDateTime date) {
        LocalDateTime now = LocalDateTime.now();
        if (date.isBefore(now.plusHours(2))) {
            log.warn("Дата события должна быть не менее чем через 2 часа от текущего времени");
            throw new ConflictException("Дата события должна быть не менее чем через 2 часа от текущего времени");
        }
    }
}
