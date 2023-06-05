package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    User getUser(Long id);

    User createUser(User user);

    User updateUser(User user, Long id);

    void deleteUser(Long id);
}
