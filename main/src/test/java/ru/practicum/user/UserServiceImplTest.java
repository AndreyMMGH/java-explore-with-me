package ru.practicum.user;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.MainServiceApp;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@ActiveProfiles("test")
@SpringBootTest(
        classes = MainServiceApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceImplTest {

    @Autowired
    private EntityManager em;

    @Autowired
    private UserService userService;

    private Long userId;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setEmail("Max@mail.com");
        user.setName("Макс");

        em.persist(user);
        em.flush();

        userId = user.getId();
    }

    @Test
    void mustCreateUser() {
        NewUserRequest newUser = new NewUserRequest("Petr@mail.com", "Петр");

        UserDto created = userService.createUser(newUser);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getEmail()).isEqualTo("Petr@mail.com");
        assertThat(created.getName()).isEqualTo("Петр");
    }

    @Test
    void mustThrowConflictExceptionWhenCreatingDuplicateUser() {
        NewUserRequest duplicate = new NewUserRequest("Max@mail.com", "Макс");

        assertThrows(ConflictException.class, () -> userService.createUser(duplicate));
    }

    @Test
    void mustReturnUsers() {
        List<UserDto> users = userService.getUsers(null, 0, 10);

        assertThat(users.get(0).getId()).isEqualTo(userId);
        assertThat(users.get(0).getEmail()).isEqualTo("Max@mail.com");
    }

    @Test
    void mustDeleteUser() {
        userService.deleteUser(userId);

        assertThrows(NotFoundException.class, () -> userService.deleteUser(userId));
    }

    @Test
    void shouldThrowNotFoundExceptionWhenDeletingNonExistingUser() {
        assertThrows(NotFoundException.class, () -> userService.deleteUser(10000L));
    }

    @Test
    void mustReturnPaginatedUsers() {
        User u2 = new User();
        u2.setEmail("second@mail.com");
        u2.setName("Евгений");
        em.persist(u2);
        em.flush();

        List<UserDto> users = userService.getUsers(null, 0, 1);
        assertThat(users.get(0).getEmail()).isEqualTo("Max@mail.com");
    }
}
