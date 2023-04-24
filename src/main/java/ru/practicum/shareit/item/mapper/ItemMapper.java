package ru.practicum.shareit.item.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Component
@AllArgsConstructor
public class ItemMapper {
    private final Validator validator;
    private final UserService userService;

    public ItemDto toDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public Item toEntity(ItemDto itemDto, Long userId) {
        Item item = Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();

        User user = userService.getUser(userId);
        item.setOwner(user);

        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<Item> validation: violations) {
                throw new ValidationException(validation.getMessage());
            }
        }

        return item;
    }

    public ItemDto patch(ItemDto itemDto, ItemDto putchItemDto) {
        if (putchItemDto.getName() != null) {
            itemDto.setName(putchItemDto.getName());
        }

        if (putchItemDto.getDescription() != null) {
            itemDto.setDescription(putchItemDto.getDescription());
        }

        if (putchItemDto.getAvailable() != null) {
            itemDto.setAvailable(putchItemDto.getAvailable());
        }

        return itemDto;
    }
}
