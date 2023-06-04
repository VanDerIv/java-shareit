package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestService itemRequestService;
    private final UserService userService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestBody final ItemRequestDto itemRequestDto,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemRequest itemRequest = itemRequestMapper.toEntity(itemRequestDto, userId);
        itemRequest = itemRequestService.createRequest(itemRequest);
        return ItemRequestMapper.toDto(itemRequest);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        User user = userService.getUser(userId);
        return itemRequestService.getUserRequests(user).stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOtherRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size) {
        User user = userService.getUser(userId);
        return itemRequestService.getOtherRequests(user, from, size).stream()
                .map(ItemRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ItemRequestDto getItemRequest(@PathVariable final Long id,
                                        @RequestHeader("X-Sharer-User-Id") Long userId) {
        userService.getUser(userId);
        ItemRequest itemRequest = itemRequestService.getRequest(id);
        return ItemRequestMapper.toDto(itemRequest);
    }
}
