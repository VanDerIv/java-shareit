package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemService {
    List<Item> getUserItems(User user);

    List<Item> findAvailableItems(User user, String search);

    Item getItem(Long id);

    Item createItem(Item item);

    Item updateItem(Item item);

    void deleteItem(User user, Long id);
}
