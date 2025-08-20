package ru.practicum.compilation;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.MainServiceApp;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.user.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
@ContextConfiguration(classes = MainServiceApp.class)
class CompilationDtoTest {

    @Autowired
    private JacksonTester<CompilationDto> jsonCompilationDto;

    @Autowired
    private JacksonTester<NewCompilationDto> jsonNewCompilationDto;

    @Autowired
    private JacksonTester<UpdateCompilationRequest> jsonUpdateCompilationRequest;

    @Test
    void shouldReturnCompilationDto() throws Exception {
        EventShortDto eventShortDto = new EventShortDto(
                10L,
                "Интересный концерт",
                new CategoryDto(1L, "Музыка"),
                50L,
                LocalDateTime.of(2025, 1, 1, 19, 0),
                new UserShortDto(5L, "Макс"),
                true,
                "Концерт в парке",
                1000L
        );

        CompilationDto compilationDto = new CompilationDto(
                1L,
                "Необычные фотозоны",
                true,
                Set.of(eventShortDto)
        );

        JsonContent<CompilationDto> result = jsonCompilationDto.write(compilationDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.title").isEqualTo("Необычные фотозоны");
        assertThat(result).extractingJsonPathBooleanValue("$.pinned").isEqualTo(true);
        assertThat(result).extractingJsonPathArrayValue("$.events").hasSize(1);
        assertThat(result).extractingJsonPathNumberValue("$.events[0].id").isEqualTo(10);
    }

    @Test
    void shouldReturnNewCompilationDto() throws Exception {
        NewCompilationDto dto = new NewCompilationDto(
                Set.of(100L, 200L),
                false,
                "Подборка сериалов"
        );

        JsonContent<NewCompilationDto> result = jsonNewCompilationDto.write(dto);

        assertThat(result).extractingJsonPathArrayValue("$.events").containsExactlyInAnyOrder(100, 200);
        assertThat(result).extractingJsonPathBooleanValue("$.pinned").isEqualTo(false);
        assertThat(result).extractingJsonPathStringValue("$.title").isEqualTo("Подборка сериалов");
    }

    @Test
    void shouldReturnUpdateCompilationRequest() throws Exception {
        UpdateCompilationRequest dto = new UpdateCompilationRequest(
                Set.of(300L),
                true,
                "Обновленная подборка"
        );

        JsonContent<UpdateCompilationRequest> result = jsonUpdateCompilationRequest.write(dto);

        assertThat(result).extractingJsonPathArrayValue("$.events").containsExactly(300);
        assertThat(result).extractingJsonPathBooleanValue("$.pinned").isEqualTo(true);
        assertThat(result).extractingJsonPathStringValue("$.title").isEqualTo("Обновленная подборка");
    }
}