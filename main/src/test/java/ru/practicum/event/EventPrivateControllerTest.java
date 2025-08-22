package ru.practicum.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.error.ErrorHandler;
import ru.practicum.event.controller.EventPrivateController;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventService;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.status.RequestStatus;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EventPrivateControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventPrivateController eventPrivateController;

    private MockMvc mvc;

    private EventFullDto eventFullDto;
    private EventShortDto eventShortDto;
    private NewEventDto newEventDto;
    private UpdateEventUserRequest updateEventUserRequest;
    private EventRequestStatusUpdateRequest updateRequest;
    private EventRequestStatusUpdateResult updateResult;

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();

        mvc = MockMvcBuilders
                .standaloneSetup(eventPrivateController)
                .setControllerAdvice(new ErrorHandler())
                .build();

        CategoryDto categoryDto = new CategoryDto(1L, "Категория");
        UserShortDto userShortDto = new UserShortDto(1L, "Макс Иванов");
        LocationDto locationDto = new LocationDto(50.01f, 31.25f);

        eventFullDto = new EventFullDto(1L,
                "Аннотация",
                categoryDto,
                5L,
                LocalDateTime.now(),
                "Описание",
                LocalDateTime.now().plusDays(1),
                userShortDto,
                locationDto,
                true,
                10,
                LocalDateTime.now(),
                true,
                null,
                "Заголовок",
                100L
        );

        eventShortDto = new EventShortDto(1L,
                "Аннотация",
                null,
                5L,
                LocalDateTime.now().plusDays(1),
                null,
                true,
                "Заголовок",
                100L
        );

        newEventDto = new NewEventDto(
                "Краткое описание, сжатый пересказ сути",
                1L,
                "Наглядное представление о предмете",
                LocalDateTime.now().plusDays(1),
                locationDto,
                true,
                10,
                true,
                "Заголовок"
        );

        updateEventUserRequest = new UpdateEventUserRequest(
                "Обновленная аннотация",
                1L,
                "Обновленное описание",
                LocalDateTime.now().plusDays(2),
                null,
                true,
                15,
                true,
                null,
                "Обновленный заголовок"
        );

        updateRequest = new EventRequestStatusUpdateRequest();
        updateRequest.setStatus(RequestStatus.CONFIRMED);
        updateRequest.setRequestIds(List.of(1L, 2L));

        updateResult = new EventRequestStatusUpdateResult(
                List.of(new ParticipationRequestDto(1L, LocalDateTime.now(), 1L, 1L, RequestStatus.CONFIRMED)),
                List.of()
        );
    }

    @Test
    void mustReturnEventsByUserId() throws Exception {
        when(eventService.getEventsByUserId(eq(1L), anyInt(), anyInt()))
                .thenReturn(List.of(eventShortDto));

        mvc.perform(get("/users/1/events")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(eventShortDto.getId()))
                .andExpect(jsonPath("$[0].annotation").value(eventShortDto.getAnnotation()))
                .andExpect(jsonPath("$[0].category").doesNotExist())
                .andExpect(jsonPath("$[0].confirmedRequests").value(eventShortDto.getConfirmedRequests()))
                .andExpect(jsonPath("$[0].eventDate").value(eventShortDto.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                .andExpect(jsonPath("$[0].initiator").doesNotExist())
                .andExpect(jsonPath("$[0].paid").value(eventShortDto.getPaid()))
                .andExpect(jsonPath("$[0].title").value(eventShortDto.getTitle()))
                .andExpect(jsonPath("$[0].views").value(eventShortDto.getViews()));
    }

    @Test
    void mustCreateEvent() throws Exception {
        when(eventService.createEvent(eq(1L), any(NewEventDto.class)))
                .thenReturn(eventFullDto);

        mvc.perform(post("/users/1/events")
                        .content(mapper.writeValueAsString(newEventDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(eventFullDto.getId()))
                .andExpect(jsonPath("$.annotation").value(eventFullDto.getAnnotation()))
                .andExpect(jsonPath("$.category.id").value(eventFullDto.getCategory().getId()))
                .andExpect(jsonPath("$.category.name").value(eventFullDto.getCategory().getName()))
                .andExpect(jsonPath("$.confirmedRequests").value(eventFullDto.getConfirmedRequests()))
                .andExpect(jsonPath("$.createdOn").value(eventFullDto.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                .andExpect(jsonPath("$.description").value(eventFullDto.getDescription()))
                .andExpect(jsonPath("$.eventDate").value(eventFullDto.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                .andExpect(jsonPath("$.initiator.id").value(eventFullDto.getInitiator().getId()))
                .andExpect(jsonPath("$.initiator.name").value(eventFullDto.getInitiator().getName()))
                .andExpect(jsonPath("$.location.lat").value(eventFullDto.getLocation().getLat()))
                .andExpect(jsonPath("$.location.lon").value(eventFullDto.getLocation().getLon()))
                .andExpect(jsonPath("$.paid").value(eventFullDto.getPaid()))
                .andExpect(jsonPath("$.participantLimit").value(eventFullDto.getParticipantLimit()))
                .andExpect(jsonPath("$.publishedOn").value(eventFullDto.getPublishedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                .andExpect(jsonPath("$.requestModeration").value(eventFullDto.getRequestModeration()))
                .andExpect(jsonPath("$.state").doesNotExist())
                .andExpect(jsonPath("$.title").value(eventFullDto.getTitle()))
                .andExpect(jsonPath("$.views").value(eventFullDto.getViews()));
    }

    @Test
    void mustReturnEventByUserIdAndEventId() throws Exception {
        when(eventService.getEventByUserIdAndEventId(1L, 1L))
                .thenReturn(eventFullDto);

        mvc.perform(get("/users/1/events/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventFullDto.getId()))
                .andExpect(jsonPath("$.annotation").value(eventFullDto.getAnnotation()))
                .andExpect(jsonPath("$.category.id").value(eventFullDto.getCategory().getId()))
                .andExpect(jsonPath("$.category.name").value(eventFullDto.getCategory().getName()))
                .andExpect(jsonPath("$.confirmedRequests").value(eventFullDto.getConfirmedRequests()))
                .andExpect(jsonPath("$.createdOn").value(eventFullDto.getCreatedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                .andExpect(jsonPath("$.description").value(eventFullDto.getDescription()))
                .andExpect(jsonPath("$.eventDate").value(eventFullDto.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                .andExpect(jsonPath("$.initiator.id").value(eventFullDto.getInitiator().getId()))
                .andExpect(jsonPath("$.initiator.name").value(eventFullDto.getInitiator().getName()))
                .andExpect(jsonPath("$.location.lat").value(eventFullDto.getLocation().getLat()))
                .andExpect(jsonPath("$.location.lon").value(eventFullDto.getLocation().getLon()))
                .andExpect(jsonPath("$.paid").value(eventFullDto.getPaid()))
                .andExpect(jsonPath("$.participantLimit").value(eventFullDto.getParticipantLimit()))
                .andExpect(jsonPath("$.publishedOn").value(eventFullDto.getPublishedOn().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                .andExpect(jsonPath("$.requestModeration").value(eventFullDto.getRequestModeration()))
                .andExpect(jsonPath("$.state").doesNotExist())
                .andExpect(jsonPath("$.title").value(eventFullDto.getTitle()))
                .andExpect(jsonPath("$.views").value(eventFullDto.getViews()));
    }

    @Test
    void mustUpdateEventByUserIdAndEventId() throws Exception {
        when(eventService.updateEventByUserIdAndEventId(eq(1L), eq(1L), any(UpdateEventUserRequest.class)))
                .thenReturn(eventFullDto);

        mvc.perform(patch("/users/1/events/1")
                        .content(mapper.writeValueAsString(updateEventUserRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventFullDto.getId()))
                .andExpect(jsonPath("$.annotation").value(eventFullDto.getAnnotation()))
                .andExpect(jsonPath("$.description").value(eventFullDto.getDescription()))
                .andExpect(jsonPath("$.title").value(eventFullDto.getTitle()));
    }

    @Test
    void mustReturnEventRequests() throws Exception {
        when(eventService.getEventRequestsByUserIdAndEventId(1L, 1L))
                .thenReturn(List.of());

        mvc.perform(get("/users/1/events/1/requests"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void mustChangeStatus() throws Exception {
        when(eventService.changeStatus(eq(1L), eq(1L), any(EventRequestStatusUpdateRequest.class)))
                .thenReturn(updateResult);

        mvc.perform(patch("/users/1/events/1/requests")
                        .content(mapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.confirmedRequests[0].id").value(updateResult.getConfirmedRequests().get(0).getId()))
                .andExpect(jsonPath("$.confirmedRequests[0].event").value(updateResult.getConfirmedRequests().get(0).getEvent()))
                .andExpect(jsonPath("$.confirmedRequests[0].requester").value(updateResult.getConfirmedRequests().get(0).getRequester()))
                .andExpect(jsonPath("$.confirmedRequests[0].status").value(updateResult.getConfirmedRequests().get(0).getStatus().toString()))
                .andExpect(jsonPath("$.rejectedRequests").isEmpty());
    }
}
