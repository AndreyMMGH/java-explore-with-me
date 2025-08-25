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
import ru.practicum.event.controller.EventAdminController;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EventAdminControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventAdminController eventAdminController;

    private MockMvc mvc;

    private EventFullDto eventFullDto;

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();

        mvc = MockMvcBuilders
                .standaloneSetup(eventAdminController)
                .setControllerAdvice(new ErrorHandler())
                .build();

        CategoryDto categoryDto = new CategoryDto(1L, "Категория");
        UserShortDto userShortDto = new UserShortDto(1L, "Макс Иванов");
        LocationDto locationDto = new LocationDto(50.01f, 31.25f);

        eventFullDto = new EventFullDto(
                1L,
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
    }

    @Test
    void mustReturnAdminEvents() throws Exception {
        List<String> states = List.of("PUBLISHED");
        List<Long> users = List.of(1L, 2L);
        List<Long> categories = List.of(1L, 2L);

        when(eventService.getAdminEvents(
                eq(users), eq(states), eq(categories),
                any(), any(), eq(0), eq(10)
        )).thenReturn(List.of(eventFullDto));

        mvc.perform(get("/admin/events")
                        .param("users", "1", "2")
                        .param("states", "PUBLISHED")
                        .param("categories", "1", "2")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(eventFullDto.getId()))
                .andExpect(jsonPath("$[0].annotation").value(eventFullDto.getAnnotation()))
                .andExpect(jsonPath("$[0].category.id").value(eventFullDto.getCategory().getId()))
                .andExpect(jsonPath("$[0].category.name").value(eventFullDto.getCategory().getName()))
                .andExpect(jsonPath("$[0].description").value(eventFullDto.getDescription()))
                .andExpect(jsonPath("$[0].title").value(eventFullDto.getTitle()));
    }

    @Test
    void mustUpdateEventByAdmin() throws Exception {
        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
        updateRequest.setAnnotation("Обновленная аннотация");
        updateRequest.setDescription("Обновленное описание");
        updateRequest.setTitle("Обновленный заголовок");

        when(eventService.updateEventByAdmin(eq(1L), any(UpdateEventAdminRequest.class)))
                .thenReturn(eventFullDto);

        mvc.perform(patch("/admin/events/1")
                        .content(mapper.writeValueAsString(updateRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(eventFullDto.getId()))
                .andExpect(jsonPath("$.annotation").value(eventFullDto.getAnnotation()))
                .andExpect(jsonPath("$.description").value(eventFullDto.getDescription()))
                .andExpect(jsonPath("$.title").value(eventFullDto.getTitle()));
    }
}
