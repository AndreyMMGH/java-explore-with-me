package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.MainServiceApp;
import ru.practicum.event.dto.*;
import ru.practicum.request.status.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@ContextConfiguration(classes = MainServiceApp.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EventDtoTest {

    private final JacksonTester<EventFullDto> jsonEventFullDto;
    private final JacksonTester<EventShortDto> jsonEventShortDto;
    private final JacksonTester<NewEventDto> jsonNewEventDto;
    private final JacksonTester<UpdateEventAdminRequest> jsonUpdateEventAdminRequest;
    private final JacksonTester<UpdateEventUserRequest> jsonUpdateEventUserRequest;
    private final JacksonTester<EventRequestStatusUpdateRequest> jsonEventRequestStatusUpdateRequest;
    private final JacksonTester<EventRequestStatusUpdateResult> jsonEventRequestStatusUpdateResult;

    @Test
    void shouldReturnEventFullDto() throws Exception {
        EventFullDto dto = new EventFullDto(
                1L, "Аннотация", null, 5L,
                LocalDateTime.of(2025, 5, 1, 12, 0, 0), "Описание",
                LocalDateTime.of(2025, 5, 2, 12, 0, 0), null, null,
                true, 10, LocalDateTime.of(2025, 5, 3, 12, 0, 0),
                true, null, "Концерт в парке", 100L
        );

        JsonContent<EventFullDto> result = jsonEventFullDto.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.annotation").isEqualTo("Аннотация");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Описание");
        assertThat(result).extractingJsonPathStringValue("$.createdOn").isEqualTo("2025-05-01 12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.eventDate").isEqualTo("2025-05-02 12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.publishedOn").isEqualTo("2025-05-03 12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.title").isEqualTo("Концерт в парке");
        assertThat(result).extractingJsonPathNumberValue("$.confirmedRequests").isEqualTo(5);
        assertThat(result).extractingJsonPathNumberValue("$.participantLimit").isEqualTo(10);
        assertThat(result).extractingJsonPathNumberValue("$.views").isEqualTo(100);
        assertThat(result).extractingJsonPathBooleanValue("$.paid").isTrue();
        assertThat(result).extractingJsonPathBooleanValue("$.requestModeration").isTrue();
    }

    @Test
    void shouldReturnEventShortDto() throws Exception {
        EventShortDto dto = new EventShortDto(1L, "Аннотация", null, 5L,
                LocalDateTime.of(2025, 1, 2, 12, 0, 0), null, true, "Концерт в парке", 100L);

        JsonContent<EventShortDto> result = jsonEventShortDto.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.annotation").isEqualTo("Аннотация");
        assertThat(result).extractingJsonPathStringValue("$.title").isEqualTo("Концерт в парке");
        assertThat(result).extractingJsonPathBooleanValue("$.paid").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.views").isEqualTo(100);
    }

    @Test
    void shouldReturnNewEventDto() throws Exception {
        NewEventDto dto = new NewEventDto(
                "Аннотация",
                1L,
                "Описание",
                LocalDateTime.of(2025, 5, 2, 12, 0, 0),
                null,
                true,
                10,
                true,
                "Концерт в парке"
        );

        JsonContent<NewEventDto> result = jsonNewEventDto.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.annotation")
                .isEqualTo("Аннотация");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Описание");
        assertThat(result).extractingJsonPathStringValue("$.title")
                .isEqualTo("Концерт в парке");
    }

    @Test
    void shouldReturnUpdateEventAdminRequest() throws Exception {
        UpdateEventAdminRequest dto = new UpdateEventAdminRequest();
        dto.setAnnotation("Аннотация");
        dto.setDescription("Описание");
        dto.setTitle("Концерт в парке");

        JsonContent<UpdateEventAdminRequest> result = jsonUpdateEventAdminRequest.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.annotation")
                .isEqualTo("Аннотация");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Описание");
        assertThat(result).extractingJsonPathStringValue("$.title").isEqualTo("Концерт в парке");
    }

    @Test
    void shouldReturnUpdateEventUserRequest() throws Exception {
        UpdateEventUserRequest dto = new UpdateEventUserRequest();
        dto.setAnnotation("Аннотация");
        dto.setDescription("Описание");
        dto.setTitle("Концерт в парке");

        JsonContent<UpdateEventUserRequest> result = jsonUpdateEventUserRequest.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.annotation")
                .isEqualTo("Аннотация");
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("Описание");
        assertThat(result).extractingJsonPathStringValue("$.title").isEqualTo("Концерт в парке");
    }

    @Test
    void shouldReturnEventRequestStatusUpdateRequest() throws Exception {
        EventRequestStatusUpdateRequest dto = new EventRequestStatusUpdateRequest(
                List.of(1L, 2L), RequestStatus.CONFIRMED
        );

        JsonContent<EventRequestStatusUpdateRequest> result = jsonEventRequestStatusUpdateRequest.write(dto);

        assertThat(result).extractingJsonPathArrayValue("$.requestIds").containsExactly(1, 2);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("CONFIRMED");
    }

    @Test
    void shouldReturnEventRequestStatusUpdateResult() throws Exception {
        EventRequestStatusUpdateResult dto = new EventRequestStatusUpdateResult(
                List.of(), List.of()
        );

        JsonContent<EventRequestStatusUpdateResult> result = jsonEventRequestStatusUpdateResult.write(dto);

        assertThat(result).extractingJsonPathArrayValue("$.confirmedRequests").isEmpty();
        assertThat(result).extractingJsonPathArrayValue("$.rejectedRequests").isEmpty();
    }
}
