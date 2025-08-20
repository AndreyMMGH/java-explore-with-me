package ru.practicum.compilation;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.MainServiceApp;
import ru.practicum.category.model.Category;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.service.CompilationService;
import ru.practicum.event.model.Event;
import ru.practicum.event.state.EventState;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(classes = MainServiceApp.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CompilationServiceImplTest {
    private final EntityManager em;
    private final CompilationService compilationService;

    private Long compilationId;
    private Long eventId;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setName("Сериалы");
        em.persist(category);

        User user = new User();
        user.setName("Макс Иванов");
        user.setEmail("Max@mail.com");
        em.persist(user);

        Event event = new Event();
        event.setTitle("Сериал Друзья");
        event.setAnnotation("Аннотация");
        event.setDescription("Описание");
        event.setCreatedOn(LocalDateTime.now());
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setState(EventState.PUBLISHED);
        event.setCategory(category);
        event.setInitiator(user);

        em.persist(event);
        em.flush();

        eventId = event.getId();

        Compilation compilation = new Compilation();
        compilation.setTitle("Подборка сериалов");
        compilation.setPinned(true);
        compilation.setEvents(Set.of(event));

        em.persist(compilation);
        em.flush();

        compilationId = compilation.getId();
    }

    @Test
    void mustReturnCompilationById() {
        CompilationDto dto = compilationService.getCompilationById(compilationId);

        assertThat(dto.getId()).isEqualTo(compilationId);
        assertThat(dto.getTitle()).isEqualTo("Подборка сериалов");
        assertThat(dto.getPinned()).isTrue();
        ;
    }

    @Test
    void shouldThrowNotFoundExceptionForInvalidId() {
        assertThrows(NotFoundException.class, () -> compilationService.getCompilationById(999999L));
    }

    @Test
    void mustCreateCompilation() {
        NewCompilationDto newDto = new NewCompilationDto(Set.of(eventId), true, "Следующая подборка");

        CompilationDto created = compilationService.createCompilation(newDto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getTitle()).isEqualTo("Следующая подборка");
        assertThat(created.getPinned()).isTrue();
    }

    @Test
    void mustUpdateCompilation() {
        UpdateCompilationRequest updateDto = new UpdateCompilationRequest(Set.of(eventId), false, "Обновленная подборка");

        CompilationDto updated = compilationService.updateCompilation(compilationId, updateDto);

        assertThat(updated.getId()).isEqualTo(compilationId);
        assertThat(updated.getTitle()).isEqualTo("Обновленная подборка");
        assertThat(updated.getPinned()).isFalse();
    }

    @Test
    void mustUpdateCompilationWithNullRequest() {
        CompilationDto updated = compilationService.updateCompilation(compilationId, null);

        assertThat(updated.getId()).isEqualTo(compilationId);
        assertThat(updated.getTitle()).isEqualTo("Подборка сериалов");
    }

    @Test
    void mustDeleteCompilation() {
        compilationService.deleteCompilation(compilationId);

        assertThrows(NotFoundException.class, () -> compilationService.getCompilationById(compilationId));
    }

    @Test
    void mustReturnPaginatedCompilations() {
        List<CompilationDto> compilations = compilationService.getCompilations(null, 0, 10);

        assertThat(compilations).isNotEmpty();
        assertThat(compilations.get(0).getTitle()).isEqualTo("Подборка сериалов");
    }

    @Test
    void mustReturnPinnedCompilationsOnly() {
        List<CompilationDto> pinned = compilationService.getCompilations(true, 0, 10);

        assertThat(pinned).hasSize(1);
        assertThat(pinned.get(0).getPinned()).isTrue();
    }

    @Test
    void mustReturnEmptyListForUnpinnedFilter() {
        List<CompilationDto> unpinned = compilationService.getCompilations(false, 0, 10);

        assertThat(unpinned).isEmpty();
    }
}
