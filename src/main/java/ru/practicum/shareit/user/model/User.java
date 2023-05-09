package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "Email пользователя должен быть задан")
    @NotBlank(message = "Email пользователя должен быть задан")
    @Email(message = "Email должен соответствовать формату почты")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotNull(message = "Имя пользователя должено быть задано")
    @NotBlank(message = "Имя пользователя должено быть задано")
    @Column(name = "name", nullable = false)
    private String name;
}
