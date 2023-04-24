package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;

    @Override
    public List<Item> getUserItems(User user) {
        return itemStorage.getUserItems(user);
    }

    @Override
    public List<Item> findAvailableItems(User user, String search) {
        return itemStorage.findAvailableItems(user, search);
    }

    @Override
    public Item getItem(Long id) {
        Optional<Item> optionalItem = itemStorage.getItem(id);
        return optionalItem.orElseThrow(() -> new NotFoundException(String.format("Вещь с id=%d не найдена", id)));
    }

    @Override
    public Item createItem(Item item) {
        return itemStorage.createItem(item);
    }

    @Override
    public Item updateItem(Item item) {
        Item oldItem = getItem(item.getId());
        if (!item.getOwner().equals(oldItem.getOwner())) {
            throw new NotFoundException(String.format("Вещь с id=%d не принадлежит пользователю %d", item.getId(), item.getOwner().getId()));
        }
        return itemStorage.updateItem(item);
    }

    @Override
    public void deleteItem(User user, Long id) {
        Item item = getItem(id);
        if (!item.getOwner().equals(user)) {
            throw new NotFoundException(String.format("Вещь с id=%d у пользователя %d не найдена", id, user.getId()));
        }
        itemStorage.deleteItem(id);
    }
}
