package ru.practicum.user;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;
import ru.practicum.user.dto.mapper.UserMapper;
import ru.practicum.user.model.User;

class UserMapperTest {

    @Test
    void shouldMapUserToUserDto() {
        User user = new User(1L, "Max@mail.com", "Макс");

        UserDto result = UserMapper.toUserDto(user);

        AssertionsForClassTypes.assertThat(result.getId()).isEqualTo(1L);
        AssertionsForClassTypes.assertThat(result.getEmail()).isEqualTo("Max@mail.com");
        AssertionsForClassTypes.assertThat(result.getName()).isEqualTo("Макс");
    }

    @Test
    void shouldMapNewUserRequestToUser() {
        NewUserRequest newUserRequest = new NewUserRequest("Max@mail.com", "Макс");

        User result = UserMapper.toUser(newUserRequest);

        AssertionsForClassTypes.assertThat(result.getId()).isNull();
        AssertionsForClassTypes.assertThat(result.getEmail()).isEqualTo("Max@mail.com");
        AssertionsForClassTypes.assertThat(result.getName()).isEqualTo("Макс");
    }

    @Test
    void shouldMapUserToUserShortDto() {
        User user = new User(2L, "Max@mail.com", "Макс");

        UserShortDto result = UserMapper.toUserShortDto(user);

        AssertionsForClassTypes.assertThat(result.getId()).isEqualTo(2L);
        AssertionsForClassTypes.assertThat(result.getName()).isEqualTo("Макс");
    }
}