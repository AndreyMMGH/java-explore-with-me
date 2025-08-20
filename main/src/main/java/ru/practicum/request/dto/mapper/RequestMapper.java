package ru.practicum.request.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.event.model.Event;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.model.Request;
import ru.practicum.user.model.User;

@UtilityClass
public class RequestMapper {
    public static ParticipationRequestDto toParticipationRequestDto(Request request) {
        return new ParticipationRequestDto(
                request.getId(),
                request.getCreated(),
                request.getEvent().getId(),
                request.getRequester().getId(),
                request.getStatus()
        );
    }

    public static Request toRequest(ParticipationRequestDto participationRequestDto, Event event, User requester) {
        return new Request(
                participationRequestDto.getId(),
                participationRequestDto.getCreated(),
                event,
                requester,
                participationRequestDto.getStatus()
        );
    }
}
