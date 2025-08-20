package ru.practicum.category;

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
import ru.practicum.category.controller.CategoryAdminController;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;
import ru.practicum.error.ErrorHandler;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@ExtendWith(MockitoExtension.class)
class CategoryAdminControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();
    @Mock
    private CategoryService categoryService;
    @InjectMocks
    private CategoryAdminController categoryAdminController;
    private MockMvc mvc;
    private CategoryDto categoryDto;
    private NewCategoryDto newCategoryDto;

    @BeforeEach
    void setUp() {
        mapper.findAndRegisterModules();
        mvc = MockMvcBuilders
                .standaloneSetup(categoryAdminController)
                .setControllerAdvice(new ErrorHandler())
                .build();

        categoryDto = new CategoryDto(1L, "Концерты");
        newCategoryDto = new NewCategoryDto("Концерты");
    }

    @Test
    void mustCreateCategory() throws Exception {
        when(categoryService.createCategory(any()))
                .thenReturn(categoryDto);

        mvc.perform(post("/admin/categories")
                        .content(mapper.writeValueAsString(newCategoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Концерты")));
    }

    @Test
    void mustDeleteCategory() throws Exception {
        mvc.perform(delete("/admin/categories/1"))
                .andExpect(status().isNoContent());

        verify(categoryService).deleteCategory(1L);
    }

    @Test
    void mustUpdateCategory() throws Exception {
        CategoryDto updated = new CategoryDto(1L, "Фестивали");
        when(categoryService.updateCategory(eq(1L), any(NewCategoryDto.class)))
                .thenReturn(updated);

        mvc.perform(patch("/admin/categories/1")
                        .content(mapper.writeValueAsString(newCategoryDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Фестивали")));
    }
}
