package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping
    public List<UserDto> getUsers() {
        List<User> users = userService.getUsers();
        return users.stream().map(userMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable final Long id) {
        User user = userService.getUser(id);
        return userMapper.toDto(user);
    }

    @PostMapping
    public UserDto createUser(@RequestBody final UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        user = userService.createUser(user);
        return userMapper.toDto(user);
    }

    @PatchMapping("/{id}")
    public UserDto patchUser(@RequestBody final UserDto userDto, @PathVariable final Long id) {
        UserDto oldUserDto = getUser(id);
        UserDto newUserDto = userMapper.putch(oldUserDto, userDto);
        User newUser = userMapper.toEntity(newUserDto);
        newUser = userService.updateUser(newUser, id);
        return userMapper.toDto(newUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable final Long id) {
        userService.deleteUser(id);
    }

}
