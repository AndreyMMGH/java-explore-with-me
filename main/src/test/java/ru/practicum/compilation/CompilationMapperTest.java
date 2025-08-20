package ru.practicum.compilation;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import ru.practicum.category.model.Category;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.model.Event;
import ru.practicum.event.state.EventState;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class CompilationMapperTest {
    @Test
    void shouldMapCompilationToCompilationDto() {
        Category category = new Category(1L, "Сериал");

        User user = new User();
        user.setId(10L);
        user.setName("Макс Иванов");
        user.setEmail("Max@example.com");

        Event event = new Event();
        event.setId(100L);
        event.setCategory(category);
        event.setInitiator(user);
        event.setTitle("Друзья");
        event.setAnnotation("Аннотация");
        event.setDescription("Описание");
        event.setCreatedOn(LocalDateTime.now());
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setState(EventState.PUBLISHED);

        Set<Event> events = new HashSet<>();
        events.add(event);

        Compilation compilation = new Compilation(
                1L,
                "Подборка сериалов",
                true,
                events
        );

        CompilationDto result = CompilationMapper.toCompilationDto(compilation);

        AssertionsForClassTypes.assertThat(result.getId()).isEqualTo(1L);
        AssertionsForClassTypes.assertThat(result.getTitle()).isEqualTo("Подборка сериалов");
        AssertionsForClassTypes.assertThat(result.getPinned()).isTrue();
    }

    @Test
    void shouldMapNewCompilationDtoToCompilationPinnedTrue() {
        NewCompilationDto dto = new NewCompilationDto(Set.of(), true, "Подборка сериалов");

        Compilation result = CompilationMapper.toCompilation(dto);

        AssertionsForClassTypes.assertThat(result.getId()).isNull();
        AssertionsForClassTypes.assertThat(result.getTitle()).isEqualTo("Подборка сериалов");
        AssertionsForClassTypes.assertThat(result.getPinned()).isTrue();
    }

    @Test
    void shouldMapNewCompilationDtoToCompilationPinnedNull() {
        NewCompilationDto dto = new NewCompilationDto(Set.of(), null, "Подборка сериалов");

        Compilation result = CompilationMapper.toCompilation(dto);

        AssertionsForClassTypes.assertThat(result.getId()).isNull();
        AssertionsForClassTypes.assertThat(result.getTitle()).isEqualTo("Подборка сериалов");
        AssertionsForClassTypes.assertThat(result.getPinned()).isFalse();
    }
}
