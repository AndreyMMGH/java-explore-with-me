package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.request.dto.ParticipationRequestDto;

import java.util.List;

public interface EventService {
    EventFullDto createEvent(Long userId, NewEventDto newEventDto);

    List<EventShortDto> getEventsByUserId(Long userId, Integer from, Integer size);

    EventFullDto getEventByUserIdAndEventId(Long userId, Long eventId);

    EventFullDto updateEventByUserIdAndEventId(Long userId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    List<ParticipationRequestDto> getEventRequestsByUserIdAndEventId(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeStatus(final Long userId,
                                                final Long eventId,
                                                final EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}
