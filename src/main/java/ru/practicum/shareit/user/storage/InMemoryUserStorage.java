package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    private Long id = 1L;

    private Long genID() {
        return id++;
    }

    @Override
    public List<User> getUsers() {
        log.info("Возращено пользователей " + users.size());
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUser(Long id) {
        if (!users.containsKey(id)) {
            log.error(String.format("Пользователь с id=%d не найден", id));
            return Optional.empty();
        }
        log.info(String.format("Пользователь с id=%d успешно возвращен", id));
        return Optional.of(users.get(id));
    }

    @Override
    public User createUser(User user) {
        user.setId(genID());
        return updateUser(user);
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.info("Пользователь добавлен/изменен " + user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        if (!users.containsKey(id)) {
            log.warn(String.format("Пользователь с id=%d не найден", id));
            return;
        }
        log.info(String.format("Пользователь с id=%d успешно удален", id));
        users.remove(id);
    }
}
