package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.shareit.GlobalProperties.DATE_FORMAT;
import static ru.practicum.shareit.GlobalProperties.DATE_ZONE_ID;

@Component("itemRequestMapper")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestMapper {
    private final Validator validator;
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

        ItemRequest itemRequest = ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requestor(requestor)
                .created(LocalDateTime.now(DATE_ZONE_ID))
                .build();

        Set<ConstraintViolation<ItemRequest>> violations = validator.validate(itemRequest);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<ItemRequest> validation: violations) {
                throw new ValidationException(validation.getMessage());
            }
        }

        return itemRequest;
    }
}
