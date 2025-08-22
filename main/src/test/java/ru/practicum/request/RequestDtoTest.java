package ru.practicum.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.status.RequestStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class RequestDtoTest {

    @Autowired
    private JacksonTester<ParticipationRequestDto> jsonParticipationRequestDto;

    @Test
    void shouldReturnParticipationRequestDto() throws Exception {
        LocalDateTime created = LocalDateTime.of(2025, 1, 1, 12, 30, 45);
        ParticipationRequestDto dto = new ParticipationRequestDto(
                10L,
                created,
                1L,
                5L,
                RequestStatus.CONFIRMED
        );

        JsonContent<ParticipationRequestDto> result = jsonParticipationRequestDto.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(10);
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2025-01-01 12:30:45");
        assertThat(result).extractingJsonPathNumberValue("$.event").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requester").isEqualTo(5);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("CONFIRMED");
    }
}
