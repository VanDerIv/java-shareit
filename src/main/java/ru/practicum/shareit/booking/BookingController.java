package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingRequestStates;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
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
                                            @RequestParam(defaultValue = "ALL") BookingRequestStates state) {
        User user = userService.getUser(userId);
        List<Booking> bookings = bookingService.getUserBooking(user, state);
        return bookings.stream().map(BookingMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "ALL") BookingRequestStates state) {
        User user = userService.getUser(userId);
        List<Booking> bookings = bookingService.getUserItemsBookings(user, state);
        return bookings.stream().map(BookingMapper::toDto).collect(Collectors.toList());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse notCurrentState(final HttpServletRequest req, final IllegalArgumentException e) {
        Map<String, String[]> params = req.getParameterMap();
        if (params.containsKey("state") && params.get("state").length > 0) {
            return new ErrorResponse("Unknown state: " + params.get("state")[0]);
        }
        return new ErrorResponse(e.getMessage());
    }
}
