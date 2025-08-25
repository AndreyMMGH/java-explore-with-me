package ru.practicum.request;

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
import ru.practicum.event.model.Event;
import ru.practicum.event.state.EventState;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.location.model.Location;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;
import ru.practicum.request.status.RequestStatus;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(classes = MainServiceApp.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceImplTest {
    private final EntityManager em;
    private final RequestService requestService;

    private User user;
    private User initiator;
    private Category category;
    private Location location;
    private Event event;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setName("Концерт");
        em.persist(category);

        location = new Location();
        location.setLat(50.01f);
        location.setLon(31.25f);
        em.persist(location);

        user = new User();
        user.setName("Макс Иванов");
        user.setEmail("Max@mail.com");
        em.persist(user);

        initiator = new User();
        initiator.setName("Ирина Дубова");
        initiator.setEmail("Iren@mail.com");
        em.persist(initiator);

        event = new Event();
        event.setTitle("Концерт в парке");
        event.setAnnotation("Аннотация");
        event.setDescription("Описание");
        event.setCategory(category);
        event.setLocation(location);
        event.setInitiator(initiator);
        event.setCreatedOn(LocalDateTime.now());
        event.setEventDate(LocalDateTime.now().plusDays(1));
        event.setState(EventState.PUBLISHED);
        event.setParticipantLimit(1);
        em.persist(event);

        em.flush();
    }

    @Test
    void mustCreateRequest() {
        ParticipationRequestDto requestDto = requestService.createRequest(user.getId(), event.getId());

        assertThat(requestDto.getId()).isNotNull();
        assertThat(requestDto.getEvent()).isEqualTo(event.getId());
        assertThat(requestDto.getRequester()).isEqualTo(user.getId());
        assertThat(requestDto.getStatus()).isEqualTo(RequestStatus.PENDING);
    }

    @Test
    void shouldThrowConflictWhenUserIsInitiator() {
        assertThrows(ConflictException.class,
                () -> requestService.createRequest(initiator.getId(), event.getId()));
    }

    @Test
    void shouldThrowConflictWhenEventNotPublished() {
        event.setState(EventState.PENDING);
        em.flush();

        assertThrows(ConflictException.class,
                () -> requestService.createRequest(user.getId(), event.getId()));
    }

    @Test
    void shouldThrowConflictWhenDuplicateRequest() {
        requestService.createRequest(user.getId(), event.getId());

        assertThrows(ConflictException.class,
                () -> requestService.createRequest(user.getId(), event.getId()));
    }

    @Test
    void mustReturnRequestsByUserId() {
        ParticipationRequestDto requestDto = requestService.createRequest(user.getId(), event.getId());

        List<ParticipationRequestDto> requests = requestService.getRequestsByUserId(user.getId());

        assertThat(requests).hasSize(1);
        assertThat(requests.get(0).getId()).isEqualTo(requestDto.getId());
    }

    @Test
    void mustCancelRequest() {
        ParticipationRequestDto requestDto = requestService.createRequest(user.getId(), event.getId());

        ParticipationRequestDto canceled = requestService.cancelRequest(user.getId(), requestDto.getId());

        assertThat(canceled.getStatus()).isEqualTo(RequestStatus.CANCELED);
    }

    @Test
    void shouldThrowNotFoundWhenCancelingOtherUserRequest() {
        ParticipationRequestDto requestDto = requestService.createRequest(user.getId(), event.getId());

        assertThrows(NotFoundException.class,
                () -> requestService.cancelRequest(initiator.getId(), requestDto.getId()));
    }
}

