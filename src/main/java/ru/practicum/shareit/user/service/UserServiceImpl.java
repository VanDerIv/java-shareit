package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ConflictException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User getUser(Long id) {
        Optional<User> optionalUser = userStorage.getUser(id);
        return optionalUser.orElseThrow(() -> new NotFoundException(String.format("Пользователь с id=%d не найден", id)));
    }

    public User createUser(User user) {
        checkDoubleEmail(user.getEmail());
        return userStorage.createUser(user);
    }

    public User updateUser(User user, Long id) {
        checkDoubleEmail(user.getEmail(), id);
        return userStorage.updateUser(user);
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
            throw new ConflictException(String.format("Пользователь с email=%s уже существует", email));
        }
    }
}
