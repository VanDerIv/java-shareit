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
        return users.stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable final Long id) {
        User user = userService.getUser(id);
        return UserMapper.toDto(user);
    }

    @PostMapping
    public UserDto createUser(@RequestBody final UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        user = userService.createUser(user);
        return UserMapper.toDto(user);
    }

    @PatchMapping("/{id}")
    public UserDto patchUser(@RequestBody final UserDto userDto, @PathVariable final Long id) {
        UserDto oldUserDto = getUser(id);
        UserDto newUserDto = userMapper.patch(oldUserDto, userDto);
        User newUser = userMapper.toEntity(newUserDto);
        newUser = userService.updateUser(newUser, id);
        return UserMapper.toDto(newUser);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable final Long id) {
        userService.deleteUser(id);
    }

}
