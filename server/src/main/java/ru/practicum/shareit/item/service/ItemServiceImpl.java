package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Primary
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Item> getUserItems(User user, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemRepository.findByOwner(user, page);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> findAvailableItems(User user, String search, Integer from, Integer size) {
        if (search.isEmpty() || search.isBlank()) return new ArrayList<>();
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        return itemRepository.findByAvailableTrueAndNameContainsIgnoreCaseOrDescriptionContainsIgnoreCase(search, page);
    }

    @Override
    @Transactional(readOnly = true)
    public Item getItem(Long id) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        return optionalItem.orElseThrow(() -> new NotFoundException(String.format("Вещь с id=%d не найдена", id)));
    }

    @Override
    @Transactional
    public Item createItem(Item item) {
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public Item updateItem(Item item) {
        Item oldItem = getItem(item.getId());
        if (!item.getOwner().equals(oldItem.getOwner())) {
            throw new NotFoundException(String.format("Вещь с id=%d не принадлежит пользователю %d", item.getId(), item.getOwner().getId()));
        }
        return itemRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteItem(User user, Long id) {
        Item item = getItem(id);
        if (!item.getOwner().equals(user)) {
            throw new NotFoundException(String.format("Вещь с id=%d у пользователя %d не найдена", id, user.getId()));
        }
        itemRepository.deleteById(id);
    }
}
