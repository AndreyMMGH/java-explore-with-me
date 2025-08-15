package ru.practicum;

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
import ru.practicum.category.controller.CategoryPublicController;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.service.CategoryService;
import ru.practicum.error.ErrorHandler;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CategoryPublicControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private CategoryService categoryService;
    @InjectMocks
    private CategoryPublicController categoryController;
    private MockMvc mvc;
    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();

        mvc = MockMvcBuilders
                .standaloneSetup(categoryController)
                .setControllerAdvice(new ErrorHandler())
                .build();

        categoryDto = new CategoryDto(1L, "Кино");
    }

    @Test
    void mustReturnAllCategories() throws Exception {
        List<CategoryDto> categories = List.of(
                new CategoryDto(1L, "Кино"),
                new CategoryDto(2L, "Фестиваль")
        );

        when(categoryService.getCategories(anyInt(), anyInt()))
                .thenReturn(categories);

        mvc.perform(get("/categories")
                        .param("from", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Кино")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Фестиваль")));
    }

    @Test
    void mustReturnCategoryById() throws Exception {
        when(categoryService.getCategory(1L))
                .thenReturn(categoryDto);

        mvc.perform(get("/categories/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Кино")));
    }
}
