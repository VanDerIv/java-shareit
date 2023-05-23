package ru.practicum.shareit.item.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentShortDto {
    private Long id;
    private String text;
    private Long itemId;
    private String authorName;
    private String created;
}
