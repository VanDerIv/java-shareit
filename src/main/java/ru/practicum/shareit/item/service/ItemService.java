package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemService {
    List<Item> getUserItems(User user, Integer from, Integer size);

    List<Item> findAvailableItems(User user, String search, Integer from, Integer size);

    Item getItem(Long id);

    Item createItem(Item item);

    Item updateItem(Item item);

    void deleteItem(User user, Long id);
}
