package ru.practicum.shareit.user.dto;

import lombok.*;

@Getter
@Setter
@Builder
public class UserDto {
    private long id;
    private String email;
    private String name;
}
