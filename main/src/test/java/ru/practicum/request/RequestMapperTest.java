package ru.practicum.request;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import ru.practicum.event.model.Event;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.status.RequestStatus;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

public class RequestMapperTest {
    @Test
    void shouldRequestToParticipationRequestDto() {
        LocalDateTime created = LocalDateTime.of(2025, 1, 1, 12, 0, 0);

        User requester = new User(2L, "Max@mail.com", "Макс Иванов");
        Event event = new Event();
        event.setId(3L);

        Request request = new Request(
                1L,
                created,
                event,
                requester,
                RequestStatus.PENDING
        );

        ParticipationRequestDto result = RequestMapper.toParticipationRequestDto(request);

        AssertionsForClassTypes.assertThat(result.getId()).isEqualTo(1L);
        AssertionsForClassTypes.assertThat(result.getCreated()).isEqualTo(created);
        AssertionsForClassTypes.assertThat(result.getEvent()).isEqualTo(3L);
        AssertionsForClassTypes.assertThat(result.getRequester()).isEqualTo(2L);
        AssertionsForClassTypes.assertThat(result.getStatus()).isEqualTo(RequestStatus.PENDING);
    }
}
