package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ru.practicum.shareit.GlobalProperties.DATE_ZONE_ID;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public Booking createBooking(Booking booking) {
       return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking approveBooking(Long id, User user, Boolean approved) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        Booking booking = optionalBooking.orElseThrow(() -> new NotFoundException(String.format("Бронирование с id=%d не найдено", id)));
        if (booking.getItem().getOwner() != user) {
            throw new NotFoundException(String.format("Бронирование %d не относится к вещям пользователя %d", id, user.getId()));
        }

        if (booking.getStatus() == BookingStatus.APPROVED) {
            throw new ValidationException("Нельзя изменить статус уже подтвержденному бронированию");
        }

        booking.setStatus(BookingStatus.REJECTED);
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getBooking(Long id, User user) {
        Optional<Booking> optionalBooking = bookingRepository.findById(id);
        Booking booking = optionalBooking.orElseThrow(() -> new NotFoundException(String.format("Бронирование с id=%d не найдено", id)));
        if (booking.getItem().getOwner() != user && booking.getBooker() != user) {
            throw new NotFoundException(String.format("Бронирование %d не относится к вещям пользователя и не является бронью пользователя %d", id, user.getId()));
        }
        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getUserBooking(User user, BookingRequestStates state, Integer from, Integer size) {
        List<Booking> bookings = bookingRepository.findByBooker(user);
        return getBookingByState(bookings, state, from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getUserItemsBookings(User user, BookingRequestStates state, Integer from, Integer size) {
        List<Booking> bookings = bookingRepository.findByItem_Owner(user);
        return getBookingByState(bookings, state, from, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getItemBookings(Item item) {
        List<Booking> bookings = bookingRepository.findByItem(item);
        return getBookingByState(bookings, BookingRequestStates.ALL, 0, 100);
    }

    private List<Booking> getBookingByState(List<Booking> bookings, BookingRequestStates state, Integer from, Integer size) {
        Stream<Booking> bookingStream = bookings.stream();
        LocalDateTime now = LocalDateTime.now(DATE_ZONE_ID);
        switch (state) {
            case CURRENT:
                bookingStream = bookingStream.filter(booking -> booking.getStart().isBefore(now)
                        && booking.getEnd().isAfter(now));
                break;
            case PAST:
                bookingStream = bookingStream.filter(booking -> booking.getEnd().isBefore(now));
                break;
            case FUTURE:
                bookingStream = bookingStream.filter(booking -> booking.getStart().isAfter(now));
                break;
            case WAITING:
                bookingStream = bookingStream.filter(booking -> booking.getStatus() == BookingStatus.WAITING);
                break;
            case REJECTED:
                bookingStream = bookingStream.filter(booking -> booking.getStatus() == BookingStatus.REJECTED);
                break;
            default:
                break;
        }

        if (from == 0) {
            return bookingStream.sorted(this::compareStartDate).limit(size).collect(Collectors.toList());
        }

        List<Booking> books = bookingStream.sorted(this::compareStartDate).collect(Collectors.toList());

        return IntStream
                .range(0, books.size())
                .filter(i -> i >= from)
                .mapToObj(books::get)
                .limit(size)
                .collect(Collectors.toList());
    }

    private int compareStartDate(Booking booking1, Booking booking2) {
        return -booking1.getStart().compareTo(booking2.getStart());
    }
}
