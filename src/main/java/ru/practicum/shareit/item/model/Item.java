package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "Имя вещи должено быть задано")
    @NotBlank(message = "Имя вещи должено быть задано")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "Описание вещи должено быть задано")
    @NotBlank(message = "Описание вещи должено быть задано")
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull(message = "Признак доступности вещи должен быть задан")
    @Column(name = "available", nullable = false)
    private Boolean available;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false) //по умолчанию проставляется owner_id
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private ItemRequest request;
}
