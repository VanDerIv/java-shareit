package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentShortDto {
    private Long id;

    @NotBlank
    private String text;

    private Long itemId;

    private String authorName;

    private String created;
}
