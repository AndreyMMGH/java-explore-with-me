package ru.practicum.category;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.MainServiceApp;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.CategoryService;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@Transactional
@ActiveProfiles("test")
@SpringBootTest(
        classes = MainServiceApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CategoryServiceImplTest {
    private final EntityManager em;
    private final CategoryService categoryService;

    private Long categoryId;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setName("Кино");

        em.persist(category);
        em.flush();

        categoryId = category.getId();
    }

    @Test
    void mustReturnCategoryById() {
        CategoryDto categoryDto = categoryService.getCategory(categoryId);

        assertThat(categoryDto.getId()).isEqualTo(categoryId);
        assertThat(categoryDto.getName()).isEqualTo("Кино");
    }

    @Test
    void shouldThrowNotFoundExceptionForInvalidId() {
        Assertions.assertThrows(NotFoundException.class, () -> categoryService.getCategory(999999L));
    }

    @Test
    void mustCreateCategory() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("Фестиваль");

        CategoryDto created = categoryService.createCategory(newCategoryDto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getName()).isEqualTo("Фестиваль");
    }

    @Test
    void mustThrowConflictExceptionWhenCreatingDuplicateCategory() {
        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName("Кино");

        Assertions.assertThrows(ConflictException.class, () -> categoryService.createCategory(newCategoryDto));
    }

    @Test
    void mustUpdateCategoryName() {
        NewCategoryDto updateDto = new NewCategoryDto();
        updateDto.setName("Спектакль");

        CategoryDto updated = categoryService.updateCategory(categoryId, updateDto);

        assertThat(updated.getId()).isEqualTo(categoryId);
        assertThat(updated.getName()).isEqualTo("Спектакль");
    }

    @Test
    void shouldThrowConflictExceptionWhenUpdatingToExistingName() {
        Category another = new Category();
        another.setName("Спектакль");
        em.persist(another);
        em.flush();

        NewCategoryDto updateDto = new NewCategoryDto();
        updateDto.setName("Спектакль");

        Assertions.assertThrows(ConflictException.class, () -> categoryService.updateCategory(categoryId, updateDto));
    }

    @Test
    void mustDeleteCategory() {
        categoryService.deleteCategory(categoryId);

        Assertions.assertThrows(NotFoundException.class, () -> categoryService.getCategory(categoryId));
    }

    @Test
    void mustReturnPaginatedCategories() {
        List<CategoryDto> categories = categoryService.getCategories(0, 10);

        assertThat(categories.get(0).getName()).isEqualTo("Кино");
    }
}
