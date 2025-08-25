package ru.practicum.request;

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
import ru.practicum.error.ErrorHandler;
import ru.practicum.request.controller.RequestPrivateController;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;
import ru.practicum.request.status.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class RequestPrivateControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private RequestService requestService;

    @InjectMocks
    private RequestPrivateController requestPrivateController;

    private MockMvc mvc;
    private ParticipationRequestDto requestDto;

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();
        mvc = MockMvcBuilders
                .standaloneSetup(requestPrivateController)
                .setControllerAdvice(new ErrorHandler())
                .build();

        requestDto = new ParticipationRequestDto(
                1L,
                LocalDateTime.of(2025, 5, 1, 12, 0, 0),
                1L,
                5L,
                RequestStatus.PENDING
        );
    }

    @Test
    void mustGetRequestsByUserId() throws Exception {
        when(requestService.getRequestsByUserId(anyLong()))
                .thenReturn(List.of(requestDto));

        mvc.perform(get("/users/5/requests")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].event", is(1)))
                .andExpect(jsonPath("$[0].requester", is(5)))
                .andExpect(jsonPath("$[0].status", is("PENDING")))
                .andExpect(jsonPath("$[0].created", is("2025-05-01 12:00:00")));
    }

    @Test
    void mustCreateRequest() throws Exception {
        when(requestService.createRequest(anyLong(), anyLong()))
                .thenReturn(requestDto);

        mvc.perform(post("/users/5/requests")
                        .param("eventId", "100")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.event", is(1)))
                .andExpect(jsonPath("$.requester", is(5)))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.created", is("2025-05-01 12:00:00")));
    }

    @Test
    void mustCancelRequest() throws Exception {
        when(requestService.cancelRequest(anyLong(), anyLong()))
                .thenReturn(requestDto);

        mvc.perform(patch("/users/5/requests/1/cancel")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.event", is(1)))
                .andExpect(jsonPath("$.requester", is(5)))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.created", is("2025-05-01 12:00:00")));
    }
}
