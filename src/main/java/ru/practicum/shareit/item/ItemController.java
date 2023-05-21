package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
    private final CommentService commentService;
    private final BookingService bookingService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                  @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        User user = userService.getUser(userId);
        List<Item> items = itemService.getUserItems(user, from, size);
        return items.stream().map(item -> ItemMapper.toDto(item, commentService.getItemComments(item), bookingService.getItemBookings(item)))
                .sorted(this::compareNextBookingDate).collect(Collectors.toList());
    }

    private int compareNextBookingDate(ItemDto itemDto1, ItemDto itemDto2) {
        if (itemDto1.getNextBooking() == null && itemDto2.getNextBooking() == null) return 0;
        if (itemDto1.getNextBooking() == null) return 1;
        if (itemDto2.getNextBooking() == null) return -1;
        return -itemDto1.getNextBooking().getStart().compareTo(itemDto2.getNextBooking().getStart());
    }

    @GetMapping("/search")
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestParam("text") String search,
                                  @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                  @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        User user = userService.getUser(userId);
        List<Item> items = itemService.findAvailableItems(user, search, from, size);
        return items.stream().map(ItemMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable final Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item item = itemService.getItem(id);
        List<Comment> comments = commentService.getItemComments(item);
        if (item.getOwner().getId() == userId) {
            return ItemMapper.toDto(item, comments, bookingService.getItemBookings(item));
        }
        return ItemMapper.toDto(item, comments);
    }

    @PostMapping
    public ItemDto createItem(@RequestBody final ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        Item item = itemMapper.toEntity(itemDto, userId);
        item = itemService.createItem(item);
        return ItemMapper.toDto(item);
    }

    @PatchMapping("/{id}")
    public ItemDto patchItem(@RequestBody final ItemDto itemDto, @PathVariable final Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemDto oldItemDto = getItem(id, userId);
        ItemDto newItemDto = itemMapper.patch(oldItemDto, itemDto);
        Item newItem = itemMapper.toEntity(newItemDto, userId);
        newItem = itemService.updateItem(newItem);
        return ItemMapper.toDto(newItem);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable final Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        User user = userService.getUser(userId);
        itemService.deleteItem(user, id);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createItemComment(@RequestBody final CommentShortDto commentShortDto, @PathVariable final Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        Comment comment = commentMapper.toEntity(commentShortDto, itemId, userId);
        comment = commentService.createComment(comment);
        return CommentMapper.toDto(commentService.getComment(comment.getId()));
    }
}
