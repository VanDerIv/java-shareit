package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentDto {
    private long id;
    private String text;
    private ItemDto item;
    private String authorName;
    private LocalDateTime created;
}
