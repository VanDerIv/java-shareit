package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mockito;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingRequestStates;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.MatcherAssert.assertThat;

public class UniBookingServiceTest {
    private BookingService bookingService;
    private final Map<Long, User> users = Map.of(
            1L, User.builder().id(1).name("test1").email("test1@test.ru").build(),
            2L, User.builder().id(2).name("test2").email("test2@test.ru").build(),
            3L, User.builder().id(3).name("test3").email("test3@test.ru").build()
    );
    private final Map<Long, Item> items = Map.of(
            1L, Item.builder()
                    .id(1)
                    .name("test item 1")
                    .description("test item 1")
                    .available(true)
                    .owner(users.get(1L))
                    .build(),
            2L, Item.builder()
                    .id(2)
                    .name("test item 2")
                    .description("test item 2")
                    .available(true)
                    .owner(users.get(2L))
                    .build(),
            3L, Item.builder()
                    .id(3)
                    .name("test item 3")
                    .description("test item 3")
                    .available(true)
                    .owner(users.get(2L))
                    .build(),
            4L, Item.builder()
                    .id(4)
                    .name("test item 4")
                    .description("test item 4")
                    .available(true)
                    .owner(users.get(2L))
                    .build()
    );
    private final Map<Long, Booking> bookings = Map.of(
            1L, Booking.builder()
                    .id(1)
                    .start(LocalDateTime.of(2023, 1, 1, 10, 0))
                    .end(LocalDateTime.of(2029, 1, 2, 10, 0))
                    .booker(users.get(2L))
                    .item(items.get(1L))
                    .status(BookingStatus.WAITING)
                    .build(),
            2L, Booking.builder()
                    .id(2)
                    .start(LocalDateTime.of(2023, 2, 1, 10, 0))
                    .end(LocalDateTime.of(2029, 1, 2, 10, 0))
                    .booker(users.get(1L))
                    .item(items.get(2L))
                    .status(BookingStatus.WAITING)
                    .build(),
            3L, Booking.builder()
                    .id(3)
                    .start(LocalDateTime.of(2023, 1, 1, 10, 0))
                    .end(LocalDateTime.of(2023, 1, 2, 10, 0))
                    .booker(users.get(1L))
                    .item(items.get(3L))
                    .status(BookingStatus.CANCELED)
                    .build(),
            4L, Booking.builder()
                    .id(4)
                    .start(LocalDateTime.of(2030, 1, 1, 10, 0))
                    .end(LocalDateTime.of(2030, 1, 2, 10, 0))
                    .booker(users.get(1L))
                    .item(items.get(4L))
                    .status(BookingStatus.WAITING)
                    .build()
    );


    @BeforeEach
    public void createEntities() {
        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        Mockito.when(bookingRepository.findById(1L))
                .thenReturn(Optional.of(bookings.get(1L)));
        Mockito.when(bookingRepository.findById(2L))
                .thenReturn(Optional.of(bookings.get(2L)));
        Mockito.when(bookingRepository.findById(3L))
                .thenReturn(Optional.empty());
        Mockito.when(bookingRepository.save(any(Booking.class)))
                .then(AdditionalAnswers.returnsFirstArg());

        Mockito.when(bookingRepository.findByBooker(any(User.class)))
                .thenAnswer(user -> bookings.values().stream()
                        .filter(booking -> booking.getBooker().equals(user.getArgument(0)))
                        .collect(Collectors.toList()));

        Mockito.when(bookingRepository.findByItem_Owner(any(User.class)))
                .thenAnswer(user -> bookings.values().stream()
                        .filter(booking -> booking.getItem().getOwner().equals(user.getArgument(0)))
                        .collect(Collectors.toList()));

        Mockito.when(bookingRepository.findByItem(any(Item.class)))
                .thenAnswer(item -> bookings.values().stream()
                        .filter(booking -> booking.getItem().equals(item.getArgument(0)))
                        .collect(Collectors.toList()));

        bookingService = new BookingServiceImpl(bookingRepository);
    }

    @Test
    public void approveBooking() {
        final NotFoundException exception1 = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.approveBooking(3L, users.get(1L), true));
        Assertions.assertEquals("Бронирование с id=3 не найдено", exception1.getMessage());

        Booking booking1 = bookingService.approveBooking(1L, users.get(1L), true);
        assertThat(booking1.getStatus(), equalTo(BookingStatus.APPROVED));

        Booking booking2 = bookingService.approveBooking(2L, users.get(2L), false);
        assertThat(booking2.getStatus(), equalTo(BookingStatus.REJECTED));

        final NotFoundException exception2 = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.approveBooking(1L, users.get(2L), true));
        Assertions.assertEquals("Бронирование 1 не относится к вещям пользователя 2", exception2.getMessage());

        final ValidationException exception3 = Assertions.assertThrows(
                ValidationException.class,
                () -> bookingService.approveBooking(1L, users.get(1L), false));
        Assertions.assertEquals("Нельзя изменить статус уже подтвержденному бронированию", exception3.getMessage());
    }

    @Test
    public void getBooking() {
        final NotFoundException exception1 = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(3L, users.get(1L)));
        Assertions.assertEquals("Бронирование с id=3 не найдено", exception1.getMessage());

        Booking booking1 = bookingService.getBooking(1L, users.get(1L));
        assertThat(booking1.getId(), equalTo(1L));

        final NotFoundException exception2 = Assertions.assertThrows(
                NotFoundException.class,
                () -> bookingService.getBooking(1L, users.get(3L)));
        Assertions.assertEquals("Бронирование 1 не относится к вещям пользователя и не является бронью пользователя 3", exception2.getMessage());
    }

    @Test
    public void getUserBooking() {
        List<Booking> bs = bookingService.getUserBooking(users.get(3L), BookingRequestStates.ALL, 0, 10);
        assertThat(bs.size(), equalTo(0));

        bs = bookingService.getUserBooking(users.get(1L), BookingRequestStates.ALL, 0, 10);
        assertThat(bs.size(), equalTo(3));

        bs = bookingService.getUserBooking(users.get(1L), BookingRequestStates.CURRENT, 0, 10);
        assertThat(bs.size(), equalTo(1));
        assertThat(bs.get(0).getId(), equalTo(2L));

        bs = bookingService.getUserBooking(users.get(1L), BookingRequestStates.PAST, 0, 10);
        assertThat(bs.size(), equalTo(1));
        assertThat(bs.get(0).getId(), equalTo(3L));

        bs = bookingService.getUserBooking(users.get(1L), BookingRequestStates.FUTURE, 0, 10);
        assertThat(bs.size(), equalTo(1));
        assertThat(bs.get(0).getId(), equalTo(4L));

        bs = bookingService.getUserBooking(users.get(1L), BookingRequestStates.WAITING, 0, 10);
        assertThat(bs.size(), equalTo(2));

        bs = bookingService.getUserBooking(users.get(1L), BookingRequestStates.REJECTED, 0, 10);
        assertThat(bs.size(), equalTo(0));

        bs = bookingService.getUserBooking(users.get(1L), BookingRequestStates.ALL, 0, 1);
        assertThat(bs.size(), equalTo(1));
        assertThat(bs.get(0).getId(), equalTo(4L));

        bs = bookingService.getUserBooking(users.get(1L), BookingRequestStates.ALL, 1, 1);
        assertThat(bs.size(), equalTo(1));
        assertThat(bs.get(0).getId(), equalTo(2L));
    }

    @Test
    public void getUserItemsBookings() {
        List<Booking> bs = bookingService.getUserItemsBookings(users.get(3L), BookingRequestStates.ALL, 0, 10);
        assertThat(bs.size(), equalTo(0));

        bs = bookingService.getUserItemsBookings(users.get(2L), BookingRequestStates.ALL, 0, 10);
        assertThat(bs.size(), equalTo(3));
        assertThat(bs.get(0).getId(), equalTo(4L));
        assertThat(bs.get(2).getId(), equalTo(3L));

        bs = bookingService.getUserItemsBookings(users.get(2L), BookingRequestStates.ALL, 1, 10);
        assertThat(bs.size(), equalTo(2));
        assertThat(bs.get(0).getId(), equalTo(2L));
    }

    @Test
    public void getItemBookings() {
        List<Booking> bs = bookingService.getItemBookings(items.get(1L));
        assertThat(bs.size(), equalTo(1));
        assertThat(bs.get(0).getItem().getId(), equalTo(1L));
    }
}
