package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.validate.NullOrNotBlank;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemPatchDto {
    private long id;

    @NullOrNotBlank
    private String name;

    @NullOrNotBlank
    private String description;

    private Boolean available;

    private BookingShortDto lastBooking;

    private BookingShortDto nextBooking;

    private List<CommentShortDto> comments;

    private Long requestId;
}
