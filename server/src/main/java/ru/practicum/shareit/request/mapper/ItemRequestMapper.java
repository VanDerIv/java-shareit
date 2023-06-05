package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.GlobalProperties.DATE_FORMAT;

@Component("itemRequestMapper")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestMapper {
    private final UserService userService;

    public static ItemRequestDto toDto(ItemRequest itemRequest) {
        List<Item> items = itemRequest.getItems();
        if (items == null) items = new ArrayList<>();

        List<ItemDto> itemResponseDtoList = items.stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requestorId(itemRequest.getRequestor().getId())
                .created(itemRequest.getCreated().format(DATE_FORMAT))
                .items(itemResponseDtoList)
                .build();
    }

    public ItemRequest toEntity(ItemRequestDto itemRequestDto, Long userId) {
        User requestor = userService.getUser(userId);

        return ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requestor(requestor)
                .created(LocalDateTime.now())
                .build();
    }
}
