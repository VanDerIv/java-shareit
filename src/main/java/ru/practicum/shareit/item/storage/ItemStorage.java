package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemStorage {
    List<Item> getUserItems(User user);

    List<Item> findAvailableItems(User user, String search);

    Optional<Item> getItem(Long id);

    Item createItem(Item item);

    Item updateItem(Item item);

    void deleteItem(Long id);
}
