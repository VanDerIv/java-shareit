package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long id;
    @Email
    private String email;
    private String name;
}
