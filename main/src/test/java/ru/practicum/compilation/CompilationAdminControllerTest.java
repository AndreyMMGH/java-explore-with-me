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
import ru.practicum.compilation.controller.CompilationAdminController;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.error.ErrorHandler;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CompilationAdminControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    private CompilationService compilationService;

    @InjectMocks
    private CompilationAdminController compilationAdminController;

    private MockMvc mvc;

    private CompilationDto compilationDto;
    private NewCompilationDto newCompilationDto;
    private UpdateCompilationRequest updateCompilationRequest;

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();
        mvc = MockMvcBuilders
                .standaloneSetup(compilationAdminController)
                .setControllerAdvice(new ErrorHandler())
                .build();

        compilationDto = new CompilationDto(1L, "Подборка сериалов", true, Set.of());
        newCompilationDto = new NewCompilationDto(Set.of(), true, "Подборка сериалов");
        updateCompilationRequest = new UpdateCompilationRequest(Set.of(), false, "Следующая подборка");
    }

    @Test
    void mustCreateCompilation() throws Exception {
        when(compilationService.createCompilation(any()))
                .thenReturn(compilationDto);

        mvc.perform(post("/admin/compilations")
                        .content(mapper.writeValueAsString(newCompilationDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Подборка сериалов")))
                .andExpect(jsonPath("$.pinned", is(true)));
    }

    @Test
    void mustDeleteCompilation() throws Exception {
        mvc.perform(delete("/admin/compilations/1"))
                .andExpect(status().isNoContent());

        verify(compilationService).deleteCompilation(1L);
    }

    @Test
    void mustUpdateCompilation() throws Exception {
        CompilationDto updated = new CompilationDto(1L, "Следующая подборка", false, Set.of());
        when(compilationService.updateCompilation(eq(1L), any(UpdateCompilationRequest.class)))
                .thenReturn(updated);

        mvc.perform(patch("/admin/compilations/1")
                        .content(mapper.writeValueAsString(updateCompilationRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.title", is("Следующая подборка")))
                .andExpect(jsonPath("$.pinned", is(false)));
    }
}

