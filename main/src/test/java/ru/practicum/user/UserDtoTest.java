package ru.practicum.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.MainServiceApp;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ContextConfiguration(classes = MainServiceApp.class)
@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDtoTest {

    private final JacksonTester<NewUserRequest> jsonNewUserRequest;
    private final JacksonTester<UserDto> jsonUserDto;
    private final JacksonTester<UserShortDto> jsonUserShortDto;

    @Test
    void shouldReturnNewUserRequest() throws Exception {
        NewUserRequest dto = new NewUserRequest("Max@mail.com", "Макс");

        JsonContent<NewUserRequest> result = jsonNewUserRequest.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("Max@mail.com");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Макс");
    }

    @Test
    void shouldReturnUserDto() throws Exception {
        UserDto dto = new UserDto(1L, "Max@mail.com", "Макс");

        JsonContent<UserDto> result = jsonUserDto.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("Max@mail.com");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Макс");
    }

    @Test
    void shouldReturnUserShortDto() throws Exception {
        UserShortDto dto = new UserShortDto(2L, "Макс");

        JsonContent<UserShortDto> result = jsonUserShortDto.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Макс");
    }
}
