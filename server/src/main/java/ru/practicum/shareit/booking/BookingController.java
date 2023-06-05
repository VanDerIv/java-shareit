package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingRequestStates;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Validated
public class BookingController {
    private final BookingMapper bookingMapper;
    private final BookingService bookingService;
    private final UserService userService;

    @PostMapping
    public BookingDto createBooking(@RequestBody final BookingShortDto bookingShortDto,
                             @RequestHeader("X-Sharer-User-Id") Long userId) {
        Booking booking = bookingMapper.toEntity(bookingShortDto, userId);
        booking = bookingService.createBooking(booking);
        return BookingMapper.toDto(booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable final Long bookingId,
                              @RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestParam Boolean approved) {
        User user = userService.getUser(userId);
        Booking entity = bookingService.approveBooking(bookingId, user, approved);
        return BookingMapper.toDto(entity);
    }

    @GetMapping("/{id}")
    public BookingDto getBooking(@PathVariable final Long id,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        User user = userService.getUser(userId);
        Booking entity = bookingService.getBooking(id, user);
        return BookingMapper.toDto(entity);
    }

    @GetMapping
    public List<BookingDto> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestParam(defaultValue = "ALL") BookingRequestStates state,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        User user = userService.getUser(userId);
        List<Booking> bookings = bookingService.getUserBooking(user, state, from, size);
        return bookings.stream().map(BookingMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "ALL") BookingRequestStates state,
                                                 @RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        User user = userService.getUser(userId);
        List<Booking> bookings = bookingService.getUserItemsBookings(user, state, from, size);
        return bookings.stream().map(BookingMapper::toDto).collect(Collectors.toList());
    }
}
