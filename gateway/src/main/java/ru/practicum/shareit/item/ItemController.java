package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient client;

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size) {
        return client.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findAvailableItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @RequestParam("text") String search,
                                  @PositiveOrZero  @RequestParam(defaultValue = "0") Integer from,
                                  @Positive @RequestParam(defaultValue = "10") Integer size) {
        return client.findAvailableItems(userId, search, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable final Long id,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getItem(id, userId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@Valid @RequestBody final ItemDto itemDto,
                                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.createItem(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchItem(@Valid @RequestBody final ItemDto itemDto,
                                            @PathVariable final Long id,
                                            @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.patchItem(itemDto, id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable final Long id, @RequestHeader("X-Sharer-User-Id") Long userId) {
        client.deleteItem(id, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createItemComment(@Valid @RequestBody final CommentShortDto commentShortDto,
                                                    @PathVariable final Long itemId,
                                                    @RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.createItemComment(commentShortDto, itemId, userId);
    }
}
