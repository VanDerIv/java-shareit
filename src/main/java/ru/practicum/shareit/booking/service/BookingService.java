package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface BookingService {
    Booking createBooking(Booking entity);

    Booking approveBooking(Long id, User user, Boolean approved);

    Booking getBooking(Long id, User user);

    List<Booking> getUserBooking(User user, BookingRequestStates state);

    List<Booking> getUserItemsBookings(User user, BookingRequestStates state);

    List<Booking> getItemBookings(Item item);
}
