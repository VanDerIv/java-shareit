package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.util.Set;

import static ru.practicum.shareit.GlobalProperties.DATE_FORMAT_SSS;
import static ru.practicum.shareit.GlobalProperties.DATE_ZONE_ID;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CommentMapper {
    private final Validator validator;
    private final UserService userService;
    private final ItemService itemService;

    public static CommentDto toDto(Comment comment) {
        ItemDto itemDto = ItemMapper.toDto(comment.getItem());
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .item(itemDto)
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated().format(DATE_FORMAT_SSS))
                .build();
    }

    public static CommentShortDto toShortDto(Comment comment) {
        return CommentShortDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemId(comment.getItem().getId())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated().format(DATE_FORMAT_SSS))
                .build();
    }

    public Comment toEntity(CommentShortDto commentShortDto, Long itemId, Long userId) {
        User author = userService.getUser(userId);
        Item item = itemService.getItem(itemId);

        Comment comment = Comment.builder()
                .text(commentShortDto.getText())
                .item(item)
                .author(author)
                .created(LocalDateTime.now(DATE_ZONE_ID))
                .build();

        Set<ConstraintViolation<Comment>> violations = validator.validate(comment);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<Comment> validation: violations) {
                throw new ValidationException(validation.getMessage());
            }
        }

        return comment;
    }
}
