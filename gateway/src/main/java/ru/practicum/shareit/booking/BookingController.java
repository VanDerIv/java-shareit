package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestStates;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.error.ErrorResponse;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Map;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@PostMapping
	public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
										   @RequestBody @Valid BookingShortDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.createBooking(userId, requestDto);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> approveBooking(@PathVariable final Long bookingId,
									 @RequestHeader("X-Sharer-User-Id") Long userId,
									 @RequestParam Boolean approved) {
		return bookingClient.approveBooking(userId, bookingId, approved);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
											 @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}

	@GetMapping
	public ResponseEntity<Object> getUserBookings(@RequestHeader("X-Sharer-User-Id") Long userId,
												  @RequestParam(defaultValue = "ALL") BookingRequestStates state,
												  @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
												  @Positive @RequestParam(defaultValue = "10") Integer size) {
		log.info("Get booking with state {}, userId={}, from={}, size={}", state, userId, from, size);
		return bookingClient.getUserBookings(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
												 @RequestParam(defaultValue = "ALL") BookingRequestStates state,
												 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
												 @Positive @RequestParam(defaultValue = "10") Integer size) {
		return bookingClient.getBookingsUserItems(userId, state, from, size);
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
