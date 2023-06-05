package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.validate.NullOrNotBlank;

import javax.validation.constraints.Email;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPatchDto {
    private long id;

    @NullOrNotBlank
    @Email
    private String email;

    @NullOrNotBlank
    private String name;
}
