package ru.practicum.category;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.dto.mapper.CategoryMapper;
import ru.practicum.category.model.Category;


public class CategoryMapperTest {
    @Test
    void shouldMapCategoryToCategoryDto() {
        Category category = new Category(1L, "Кино");

        CategoryDto result = CategoryMapper.toCategoryDto(category);

        AssertionsForClassTypes.assertThat(result.getId()).isEqualTo(1L);
        AssertionsForClassTypes.assertThat(result.getName()).isEqualTo("Кино");
    }

    @Test
    void shouldMapNewCategoryDtoToCategory() {
        NewCategoryDto newCategoryDto = new NewCategoryDto("Кино");

        Category result = CategoryMapper.toCategory(newCategoryDto);

        AssertionsForClassTypes.assertThat(result.getId()).isNull();
        AssertionsForClassTypes.assertThat(result.getName()).isEqualTo("Кино");
    }
}
