package ru.practicum.shareit.user.mapper;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Component
@AllArgsConstructor
public class UserMapper {
    private final Validator validator;

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }

    public User toEntity(UserDto userDto) {
        User user = User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .name(userDto.getName())
                .build();

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<User> validation: violations) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, validation.getMessage());
            }
        }

        return user;
    }

    public UserDto putch(UserDto userDto, UserDto putchUserDto) {
         if (putchUserDto.getName() != null) {
             userDto.setName(putchUserDto.getName());
         }

        if (putchUserDto.getEmail() != null) {
            userDto.setEmail(putchUserDto.getEmail());
        }

        return userDto;
    }
}
