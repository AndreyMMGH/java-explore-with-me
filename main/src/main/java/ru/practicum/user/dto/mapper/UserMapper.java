package ru.practicum.user.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.user.model.User;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserShortDto;

@UtilityClass
public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getName()
        );
    }

    public static User toUser(NewUserRequest newUserRequest) {
        return new User(
                null,
                newUserRequest.getEmail(),
                newUserRequest.getName()
        );
    }

    public static UserShortDto userShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }
}
