package ru.practicum.shareit.booking.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingShortDto {
    private long id;
    private String start;
    private String end;
    private Long itemId;
    private Long bookerId;
    private String status;
}
