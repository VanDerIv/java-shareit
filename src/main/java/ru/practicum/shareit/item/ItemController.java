package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        User user = userService.getUser(userId);
        List<Item> items = itemService.getUserItems(user);
        return items.stream().map(itemMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam("text") String search) {
        User user = userService.getUser(userId);
        List<Item> items = itemService.findAvailableItems(user, search);
        return items.stream().map(itemMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable final Long id) {
        Item item = itemService.getItem(id);
        return itemMapper.toDto(item);
    }

    @PostMapping
    public ItemDto createItem(@RequestBody final ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item item = itemMapper.toEntity(itemDto, userId);
        item = itemService.createItem(item);
        return itemMapper.toDto(item);
    }

    @PatchMapping("/{id}")
    public ItemDto patchItem(@RequestBody final ItemDto itemDto, @PathVariable final Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemDto oldItemDto = getItem(id);
        ItemDto newItemDto = itemMapper.patch(oldItemDto, itemDto);
        Item newItem = itemMapper.toEntity(newItemDto, userId);
        newItem = itemService.updateItem(newItem);
        return itemMapper.toDto(newItem);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable final Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        User user = userService.getUser(userId);
        itemService.deleteItem(user, id);
    }
}
