package ru.practicum.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.error.ErrorHandler;
import ru.practicum.event.controller.EventPublicController;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EventPublicControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventPublicController eventPublicController;

    private MockMvc mvc;

    private EventFullDto eventFullDto;
    private EventShortDto eventShortDto;

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();

        mvc = MockMvcBuilders
                .standaloneSetup(eventPublicController)
                .setControllerAdvice(new ErrorHandler())
                .build();

        CategoryDto categoryDto = new CategoryDto(1L, "Категория");
        UserShortDto userShortDto = new UserShortDto(1L, "Макс Иванов");
        LocationDto locationDto = new LocationDto(50.01f, 31.25f);

        eventFullDto = new EventFullDto(
                1L,
                "Аннотация",
                categoryDto,
                10L,
                LocalDateTime.now(),
                "Описание",
                LocalDateTime.now().plusDays(1),
                userShortDto,
                locationDto,
                true,
                100,
                LocalDateTime.now(),
                true,
                null,
                "Заголовок",
                500L
        );

        eventShortDto = new EventShortDto(
                1L,
                "Аннотация",
                categoryDto,
                10L,
                LocalDateTime.now().plusDays(1),
                userShortDto,
                true,
                "Заголовок",
                500L
        );
    }

    @Test
    void mustReturnPublicEvents() throws Exception {
        when(eventService.getEvents(
                anyString(), anyList(), anyBoolean(),
                any(), any(),
                anyBoolean(), anyString(),
                anyInt(), anyInt(),
                any(HttpServletRequest.class)
        )).thenReturn(List.of(eventShortDto));

        mvc.perform(get("/events")
                        .param("text", "текст для поиска")
                        .param("categories", "1")
                        .param("paid", "true")
                        .param("rangeStart", "2025-05-22 12:00:00")
                        .param("rangeEnd", "2025-05-23 12:00:00")
                        .param("onlyAvailable", "true")
                        .param("sort", "EVENT_DATE")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(eventShortDto.getId()))
                .andExpect(jsonPath("$[0].annotation").value(eventShortDto.getAnnotation()))
                .andExpect(jsonPath("$[0].category.id").value(eventShortDto.getCategory().getId()))
                .andExpect(jsonPath("$[0].category.name").value(eventShortDto.getCategory().getName()))
                .andExpect(jsonPath("$[0].confirmedRequests").value(eventShortDto.getConfirmedRequests()))
                .andExpect(jsonPath("$[0].eventDate").value(eventShortDto.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                .andExpect(jsonPath("$[0].initiator.id").value(eventShortDto.getInitiator().getId()))
                .andExpect(jsonPath("$[0].initiator.name").value(eventShortDto.getInitiator().getName()))
                .andExpect(jsonPath("$[0].paid").value(eventShortDto.getPaid()))
                .andExpect(jsonPath("$[0].title").value(eventShortDto.getTitle()))
                .andExpect(jsonPath("$[0].views").value(eventShortDto.getViews()));
    }

    @Test
    void mustReturnPublicEventById() throws Exception {
        when(eventService.getEventById(eq(1L), any(HttpServletRequest.class)))
                .thenReturn(eventFullDto);

        mvc.perform(get("/events/1"))
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
}
