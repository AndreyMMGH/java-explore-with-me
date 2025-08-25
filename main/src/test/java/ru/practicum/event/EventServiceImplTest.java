package ru.practicum.event;

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
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.*;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.event.state.EventState;
import ru.practicum.event.state.EventStateActionSolution;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.ValidationException;
import ru.practicum.location.dto.LocationDto;
import ru.practicum.location.model.Location;
import ru.practicum.request.model.Request;
import ru.practicum.request.status.RequestStatus;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@Transactional
@ActiveProfiles("test")
@SpringBootTest(
        classes = MainServiceApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventServiceImplTest {

    private final EntityManager em;
    private final EventService eventService;
    private Long userId;
    private Long categoryId;
    private Long locationId;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setName("Макс Иванов");
        user.setEmail("Max@mail.com");
        em.persist(user);

        userId = user.getId();

        Category category = new Category();
        category.setName("Кино");
        em.persist(category);
        categoryId = category.getId();

        Location location = new Location();
        location.setLat(50.01f);
        location.setLon(31.25f);
        em.persist(location);
        locationId = location.getId();

        em.flush();
    }

    @Test
    void mustCreateEvent() {
        NewEventDto dto = new NewEventDto(
                "Аннотация",
                categoryId,
                "Описание",
                LocalDateTime.now().plusDays(1),
                new LocationDto(50.01f, 31.25f),
                true,
                10,
                true,
                "Заголовок"
        );

        EventFullDto created = eventService.createEvent(userId, dto);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getAnnotation()).isEqualTo(dto.getAnnotation());
        assertThat(created.getTitle()).isEqualTo(dto.getTitle());
        assertThat(created.getCategory().getId()).isEqualTo(categoryId);
    }

    @Test
    void mustGetEventByUserIdAndEventId() {
        Event event = new Event();
        event.setAnnotation("Аннотация");
        event.setCategory(em.find(Category.class, categoryId));
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("Описание");
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setInitiator(em.find(User.class, userId));
        event.setLocation(em.find(Location.class, locationId));
        event.setPaid(true);
        event.setParticipantLimit(10);
        event.setRequestModeration(true);
        event.setState(EventState.PENDING);
        event.setTitle("Заголовок");

        em.persist(event);
        em.flush();

        EventFullDto found = eventService.getEventByUserIdAndEventId(userId, event.getId());

        assertThat(found.getId()).isEqualTo(event.getId());
        assertThat(found.getAnnotation()).isEqualTo(event.getAnnotation());
        assertThat(found.getTitle()).isEqualTo(event.getTitle());
    }

    @Test
    void mustUpdateEventByUser() {
        Event event = new Event();
        event.setAnnotation("Аннотация");
        event.setCategory(em.find(Category.class, categoryId));
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("Описание");
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setInitiator(em.find(User.class, userId));
        event.setLocation(em.find(Location.class, locationId));
        event.setPaid(true);
        event.setParticipantLimit(10);
        event.setRequestModeration(true);
        event.setState(EventState.PENDING);
        event.setTitle("Заголовок");

        em.persist(event);
        em.flush();

        UpdateEventUserRequest updateDto = new UpdateEventUserRequest();
        updateDto.setAnnotation("Обновленная аннотация");
        updateDto.setTitle("Обновленный заголовок");

        EventFullDto updated = eventService.updateEventByUserIdAndEventId(userId, event.getId(), updateDto);

        assertThat(updated.getAnnotation()).isEqualTo("Обновленная аннотация");
        assertThat(updated.getTitle()).isEqualTo("Обновленный заголовок");
    }

    @Test
    void shouldThrowValidationExceptionForInvalidEventDate() {
        NewEventDto dto = new NewEventDto(
                "Аннотация",
                categoryId,
                "Описание",
                LocalDateTime.now().plusMinutes(30),
                new LocationDto(50.01f, 31.25f),
                true,
                10,
                true,
                "Заголовок"
        );

        Assertions.assertThrows(ValidationException.class, () -> eventService.createEvent(userId, dto));
    }

    @Test
    void mustReturnEventsByUserId() {
        Event event = new Event();
        event.setAnnotation("Аннотация");
        event.setCategory(em.find(Category.class, categoryId));
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("Описание");
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setInitiator(em.find(User.class, userId));
        event.setLocation(em.find(Location.class, locationId));
        event.setPaid(true);
        event.setParticipantLimit(10);
        event.setRequestModeration(true);
        event.setState(EventState.PENDING);
        event.setTitle("Заголовок");

        em.persist(event);
        em.flush();

        List<EventShortDto> events = eventService.getEventsByUserId(userId, 0, 10);

        assertThat(events).isNotEmpty();
        assertThat(events.get(0).getId()).isEqualTo(event.getId());
    }

    @Test
    void mustReturnAdminEvents() {
        Event event = new Event();
        event.setAnnotation("Аннотация");
        event.setCategory(em.find(Category.class, categoryId));
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("Описание");
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setInitiator(em.find(User.class, userId));
        event.setLocation(em.find(Location.class, locationId));
        event.setPaid(true);
        event.setParticipantLimit(10);
        event.setRequestModeration(true);
        event.setState(EventState.PENDING);
        event.setTitle("Заголовок");

        em.persist(event);
        em.flush();

        List<EventFullDto> events = eventService.getAdminEvents(
                List.of(userId),
                List.of("PENDING"),
                List.of(categoryId),
                null,
                null,
                0,
                10
        );

        assertThat(events).hasSize(1);
        EventFullDto dto = events.get(0);
        assertThat(dto.getId()).isEqualTo(event.getId());
        assertThat(dto.getTitle()).isEqualTo("Заголовок");
        assertThat(dto.getCategory().getId()).isEqualTo(categoryId);
    }

    @Test
    void mustUpdateEventByAdmin() {
        Event event = new Event();
        event.setAnnotation("Аннотация");
        event.setCategory(em.find(Category.class, categoryId));
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("Описание");
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setInitiator(em.find(User.class, userId));
        event.setLocation(em.find(Location.class, locationId));
        event.setPaid(true);
        event.setParticipantLimit(10);
        event.setRequestModeration(true);
        event.setState(EventState.PENDING);
        event.setTitle("Заголовок");

        em.persist(event);
        em.flush();

        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
        updateRequest.setTitle("Обновленный заголовок");
        updateRequest.setAnnotation("Обновленная аннотация");
        updateRequest.setStateAction(EventStateActionSolution.PUBLISH_EVENT);

        EventFullDto updated = eventService.updateEventByAdmin(event.getId(), updateRequest);

        assertThat(updated.getTitle()).isEqualTo("Обновленный заголовок");
        assertThat(updated.getAnnotation()).isEqualTo("Обновленная аннотация");
        assertThat(updated.getState()).isEqualTo(EventState.PUBLISHED);
    }

    @Test
    void shouldThrowConflictExceptionWhenPublishingWrongState() {
        Event event = new Event();
        event.setAnnotation("Аннотация");
        event.setCategory(em.find(Category.class, categoryId));
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("Описание");
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setInitiator(em.find(User.class, userId));
        event.setLocation(em.find(Location.class, locationId));
        event.setPaid(true);
        event.setParticipantLimit(10);
        event.setRequestModeration(true);
        event.setState(EventState.PUBLISHED);
        event.setTitle("Заголовок");

        em.persist(event);
        em.flush();

        UpdateEventAdminRequest updateRequest = new UpdateEventAdminRequest();
        updateRequest.setStateAction(EventStateActionSolution.PUBLISH_EVENT);

        Assertions.assertThrows(ConflictException.class,
                () -> eventService.updateEventByAdmin(event.getId(), updateRequest));
    }

    @Test
    void mustChangeRequestStatus() {
        Event event = new Event();
        event.setAnnotation("Аннотация");
        event.setCategory(em.find(Category.class, categoryId));
        event.setCreatedOn(LocalDateTime.now());
        event.setDescription("Описание");
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setInitiator(em.find(User.class, userId));
        event.setLocation(em.find(Location.class, locationId));
        event.setPaid(true);
        event.setParticipantLimit(1);
        event.setRequestModeration(true);
        event.setState(EventState.PENDING);
        event.setTitle("Заголовок");

        em.persist(event);
        User requester1 = new User();
        requester1.setName("Макс Иванов");
        requester1.setEmail("Max@mail.ru");
        em.persist(requester1);

        User requester2 = new User();
        requester2.setName("Ирина Дубинина");
        requester2.setEmail("Iren@mail.ru");
        em.persist(requester2);

        Request request1 = new Request();
        request1.setEvent(event);
        request1.setRequester(requester1);
        request1.setStatus(RequestStatus.PENDING);

        Request request2 = new Request();
        request2.setEvent(event);
        request2.setRequester(requester2);
        request2.setStatus(RequestStatus.PENDING);

        em.persist(request1);
        em.persist(request2);
        em.flush();

        EventRequestStatusUpdateRequest statusRequest = new EventRequestStatusUpdateRequest();
        statusRequest.setRequestIds(List.of(request1.getId(), request2.getId()));
        statusRequest.setStatus(RequestStatus.CONFIRMED);

        EventRequestStatusUpdateResult result = eventService.changeStatus(event.getInitiator().getId(), event.getId(), statusRequest);

        assertThat(result.getConfirmedRequests()).hasSize(1);
        assertThat(result.getRejectedRequests()).hasSize(1);
    }
}