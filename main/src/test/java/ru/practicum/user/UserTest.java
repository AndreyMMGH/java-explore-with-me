package ru.practicum.user;

import org.junit.jupiter.api.Test;
import ru.practicum.user.model.User;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class UserTest {

    @Test
    void testEqualsAndHashCode() {
        User user1 = new User(1L, "test@mail.com", "Макс");
        User user2 = new User(2L, "test@mail.com", "Петр");
        User user3 = new User(3L, "other@mail.com", "Макс");

        assertThat(user1).isEqualTo(user1);
        assertThat(user1.hashCode()).isEqualTo(user1.hashCode());

        assertThat(user1).isEqualTo(user2);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());

        assertThat(user1).isNotEqualTo(user3);
        assertThat(user1.hashCode()).isNotEqualTo(user3.hashCode());

        assertThat(user1).isNotEqualTo(null);
        assertThat(user1).isNotEqualTo("string");
    }
}
