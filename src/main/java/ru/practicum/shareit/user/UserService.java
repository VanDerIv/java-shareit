package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    public List<UserDto> getUsers() {
        List<User> users = userStorage.getUsers();
        return users.stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    public UserDto getUser(Long id) {
        Optional<User> optionalUser = userStorage.getUser(id);
        User user = optionalUser.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден", id)));
        return userMapper.toDto(user);
    }

    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        checkDoubleEmail(userDto.getEmail());

        User finalUser = userStorage.createUser(user);
        return userMapper.toDto(finalUser);
    }

    public UserDto putchUser(UserDto userDto, Long id) {
        if (userDto.getEmail() != null) checkDoubleEmail(userDto.getEmail(), id);

        UserDto oldUserDto = getUser(id);
        UserDto newUserDto = userMapper.putch(oldUserDto, userDto);

        User user = userMapper.toEntity(newUserDto);
        user = userStorage.updateUser(user);
        return userMapper.toDto(user);
    }

    public void deleteUser(Long id) {
        userStorage.deleteUser(id);
    }

    private void checkDoubleEmail(String email) {
        checkDoubleEmail(email, 0L);
    }
    private void checkDoubleEmail(String email, Long id) {
        Optional<User> optionalUser = userStorage.getUsers().stream()
                .filter(u -> u.getEmail().equals(email) && u.getId() != id)
                .findFirst();
        if (optionalUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    String.format("Пользователь с email=%s уже существует", email));
        }
    }
}
