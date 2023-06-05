package ru.practicum.shareit.user.dto;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long id;

    @NotBlank(message = "Email пользователя должен быть задан")
    @Email(message = "Email должен соответствовать формату почты")
    private String email;

    @NotBlank(message = "Имя пользователя должено быть задано")
    private String name;
}
