package ru.practicum.compilation;

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
import ru.practicum.compilation.controller.CompilationPublicController;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.error.ErrorHandler;

import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CompilationPublicControllerTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private CompilationService compilationService;

    @InjectMocks
    private CompilationPublicController compilationController;

    private MockMvc mvc;

    private CompilationDto compilationDto1;
    private CompilationDto compilationDto2;

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();

        mvc = MockMvcBuilders
                .standaloneSetup(compilationController)
                .setControllerAdvice(new ErrorHandler())
                .build();

        compilationDto1 = new CompilationDto(1L, "Подборка сериалов", true, Set.of());
        compilationDto2 = new CompilationDto(2L, "Подборка фильмов", false, Set.of());
    }

    @Test
    void mustReturnAllCompilations() throws Exception {
        List<CompilationDto> compilations = List.of(compilationDto1, compilationDto2);

        when(compilationService.getCompilations(any(), anyInt(), anyInt()))
                .thenReturn(compilations);

        mvc.perform(get("/compilations")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].title", is("Подборка сериалов")))
                .andExpect(jsonPath("$[0].pinned", is(true)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].title", is("Подборка фильмов")))
                .andExpect(jsonPath("$[1].pinned", is(false)));
    }

    @Test
    void mustReturnCompilationById() throws Exception {
        when(compilationService.getCompilationById(1L))
                .thenReturn(compilationDto1);

        mvc.perform(get("/compilations/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Подборка сериалов")))
                .andExpect(jsonPath("$.pinned", is(true)));
    }
}
