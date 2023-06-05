package ru.practicum.shareit.user.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@Component("userMapper")
@AllArgsConstructor
public class UserMapper {
    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public User toEntity(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();
    }

    public UserDto patch(UserDto userDto, UserDto putchUserDto) {
         if (putchUserDto.getName() != null) {
             userDto.setName(putchUserDto.getName());
         }

        if (putchUserDto.getEmail() != null) {
            userDto.setEmail(putchUserDto.getEmail());
        }

        return userDto;
    }
}
