package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.validate.FutureOrPresentString;
import ru.practicum.shareit.validate.FutureString;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingShortDto {
    private long id;

    @FutureOrPresentString
    @NotNull
    private String start;

    @FutureString
    @NotNull
    private String end;

    @NotNull
    private Long itemId;

    private Long bookerId;

    private String status;
}
