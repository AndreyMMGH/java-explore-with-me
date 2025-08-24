package ru.practicum.event;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.state.EventState;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.model.Location;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

public class EventMapperTest {
    @Test
    void shouldMapEventToEventFullDto() {
        Category category = new Category(1L, "Концерт");
        User initiator = new User(1L, "Макс Иванов", "Max@mail.ru");
        Location location = new Location(1L, 50.01f, 31.25f);

        Event event = new Event(
                1L,
                "Аннотация",
                category,
                5L,
                LocalDateTime.of(2025, 5, 1, 12, 0),
                "Описание",
                LocalDateTime.of(2025, 5, 2, 12, 0),
                initiator,
                location,
                true,
                10,
                LocalDateTime.of(2025, 5, 3, 12, 0),
                true,
                EventState.PUBLISHED,
                "Заголовок"
        );

        EventFullDto result = EventMapper.toEventFullDto(event);

        AssertionsForClassTypes.assertThat(result.getId()).isEqualTo(1L);
        AssertionsForClassTypes.assertThat(result.getAnnotation()).isEqualTo("Аннотация");
        AssertionsForClassTypes.assertThat(result.getDescription()).isEqualTo("Описание");
        AssertionsForClassTypes.assertThat(result.getTitle()).isEqualTo("Заголовок");
        AssertionsForClassTypes.assertThat(result.getPaid()).isTrue();
        AssertionsForClassTypes.assertThat(result.getParticipantLimit()).isEqualTo(10);
        AssertionsForClassTypes.assertThat(result.getConfirmedRequests()).isEqualTo(5L);
    }

    @Test
    void shouldMapEventToEventShortDto() {
        Category category = new Category(1L, "Концерт");
        User initiator = new User(1L, "Макс Иванов", "Max@mail.ru");
        Location location = new Location(1L, 50.01f, 31.25f);

        Event event = new Event(
                1L,
                "Аннотация",
                category,
                5L,
                LocalDateTime.of(2025, 5, 1, 12, 0),
                "Описание",
                LocalDateTime.of(2025, 5, 2, 12, 0),
                initiator,
                location,
                true,
                10,
                LocalDateTime.of(2025, 5, 3, 12, 0),
                true,
                EventState.PUBLISHED,
                "Заголовок"
        );

        EventShortDto result = EventMapper.toEventShortDto(event);

        AssertionsForClassTypes.assertThat(result.getId()).isEqualTo(1L);
        AssertionsForClassTypes.assertThat(result.getAnnotation()).isEqualTo("Аннотация");
        AssertionsForClassTypes.assertThat(result.getTitle()).isEqualTo("Заголовок");
        AssertionsForClassTypes.assertThat(result.getPaid()).isTrue();
        AssertionsForClassTypes.assertThat(result.getConfirmedRequests()).isEqualTo(5L);
    }

    @Test
    void shouldMapNewEventDtoToEvent() {
        NewEventDto newEventDto = new NewEventDto(
                "Аннотация",
                1L,
                "Описание",
                LocalDateTime.of(2025, 5, 2, 12, 0),
                new LocationDto(50.01f, 31.25f),
                true,
                10,
                true,
                "Заголовок"
        );

        Category category = new Category(1L, "Концерт");
        User initiator = new User(1L, "Макс Иванов", "Max@mail.ru");

        Event result = EventMapper.toEvent(newEventDto, category, initiator);

        AssertionsForClassTypes.assertThat(result.getId()).isNull();
        AssertionsForClassTypes.assertThat(result.getAnnotation()).isEqualTo("Аннотация");
        AssertionsForClassTypes.assertThat(result.getDescription()).isEqualTo("Описание");
        AssertionsForClassTypes.assertThat(result.getTitle()).isEqualTo("Заголовок");
        AssertionsForClassTypes.assertThat(result.getPaid()).isTrue();
        AssertionsForClassTypes.assertThat(result.getParticipantLimit()).isEqualTo(10);
        AssertionsForClassTypes.assertThat(result.getConfirmedRequests()).isEqualTo(0L);
    }
}
