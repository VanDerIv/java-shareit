package ru.practicum.shareit.user.model;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @EqualsAndHashCode.Include
    private long id;

    @NotNull(message = "Email пользователя должен быть задан")
    @NotBlank(message = "Email пользователя должен быть задан")
    @Email(message = "Email должен соответствовать формату почты")
    private String email;

    @NotNull(message = "Имя пользователя должено быть задано")
    @NotBlank(message = "Имя пользователя должено быть задано")
    private String name;
}
