package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
    @EqualsAndHashCode.Include
    private long id;

    @NotNull(message = "Имя вещи должено быть задано")
    @NotBlank(message = "Имя вещи должено быть задано")
    private String name;

    @NotNull(message = "Описание вещи должено быть задано")
    @NotBlank(message = "Описание вещи должено быть задано")
    private String description;

    @NotNull(message = "Признак доступности вещи должен быть задан")
    private Boolean available;

    @NotNull
    private User owner;
}
