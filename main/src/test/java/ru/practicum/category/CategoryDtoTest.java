package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.MainServiceApp;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ContextConfiguration(classes = MainServiceApp.class)
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CategoryDtoTest {
    private final JacksonTester<CategoryDto> jsonCategoryDto;
    private final JacksonTester<NewCategoryDto> jsonNewCategoryDto;

    @Test
    void shouldReturnCategoryDto() throws Exception {
        CategoryDto dto = new CategoryDto(1L, "Выставки");

        JsonContent<CategoryDto> result = jsonCategoryDto.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Выставки");
    }

    @Test
    void shouldReturnNewCategoryDto() throws Exception {
        NewCategoryDto dto = new NewCategoryDto("Выставки");

        JsonContent<NewCategoryDto> result = jsonNewCategoryDto.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Выставки");
    }
}
