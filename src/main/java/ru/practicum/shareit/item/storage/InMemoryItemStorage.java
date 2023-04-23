package ru.practicum.shareit.item.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    private Long id = 1L;

    private Long genID() {
        return id++;
    }

    @Override
    public List<Item> getUserItems(User user) {
        List<Item> userItems = items.values().stream()
                .filter(item -> item.getOwner().equals(user))
                .collect(Collectors.toList());

        log.info(String.format("Возращено %d вещей пользователя %d", userItems.size(), user.getId()));
        return userItems;
    }

    @Override
    public List<Item> findAvailableItems(User user, String search) {
        if (search.isEmpty() || search.isBlank()) return new ArrayList<>();

        List<Item> searchItems = items.values().stream()
                .filter(item -> item.getName().toLowerCase().contains(search.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(search.toLowerCase()))
                .filter(Item::getAvailable)
                //.filter(item -> !item.getOwner().equals(user))
                .collect(Collectors.toList());
        log.info(String.format("Найдено %d вещей доступных пользователю %d для заказа по поисковому запросу %s",
                searchItems.size(), user.getId(), search));
        return searchItems;
    }

    @Override
    public Optional<Item> getItem(Long id) {
        if (!items.containsKey(id)) {
            log.error(String.format("Вещь с id=%d не найдена", id));
            return Optional.empty();
        }
        log.info(String.format("Вещь с id=%d успешно возвращена", id));
        return Optional.of(items.get(id));
    }

    @Override
    public Item createItem(Item item) {
        item.setId(genID());
        return updateItem(item);
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        log.info("Вещь добавлена/изменена " + item);
        return item;
    }

    @Override
    public void deleteItem(Long id) {
        if (!items.containsKey(id)) {
            log.warn(String.format("Вещь с id=%d не найдена", id));
            return;
        }
        log.info(String.format("Вещь с id=%d успешно удалена", id));
        items.remove(id);
    }
}
