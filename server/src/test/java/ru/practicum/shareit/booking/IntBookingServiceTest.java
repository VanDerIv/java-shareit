package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingRequestStates;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(properties = "db.name=test", webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class IntBookingServiceTest {
    private final EntityManager em;
    private final BookingService service;
    private final List<User> sourceUsers = List.of(
            User.builder().name("test1").email("test1@test.ru").build(),
            User.builder().name("test2").email("test2@test.ru").build(),
            User.builder().name("test3").email("test3@test.ru").build()
    );
    private final List<Item> sourceItems = List.of(
            Item.builder()
                    .name("test item 1")
                    .description("test item 1")
                    .available(true)
                    .build(),
            Item.builder()
                    .name("test item 2")
                    .description("test item 2")
                    .available(true)
                    .build(),
            Item.builder()
                    .name("test item 3")
                    .description("test item 3")
                    .available(true)
                    .build(),
            Item.builder()
                    .name("test item 4")
                    .description("test item 4")
                    .available(true)
                    .build()
    );
    private final List<Booking> sourceBookings = List.of(
            Booking.builder()
                    .start(LocalDateTime.now().plusSeconds(1))
                    .end(LocalDateTime.now().plusDays(2))
                    .status(BookingStatus.WAITING)
                    .build(),
            Booking.builder()
                    .start(LocalDateTime.now().plusSeconds(1))
                    .end(LocalDateTime.now().plusDays(1))
                    .status(BookingStatus.WAITING)
                    .build(),
            Booking.builder()
                    .start(LocalDateTime.now().plusSeconds(1))
                    .end(LocalDateTime.now().plusSeconds(2))
                    .status(BookingStatus.CANCELED)
                    .build(),
            Booking.builder()
                    .start(LocalDateTime.now().plusDays(1))
                    .end(LocalDateTime.now().plusDays(2))
                    .status(BookingStatus.WAITING)
                    .build()
    );

    @BeforeEach
    public void createEntities() {
        long stopSec = 2L;

        for (User user : sourceUsers) {
            em.persist(user);
        }
        em.flush();
        User user1 = sourceUsers.get(0);
        User user2 = sourceUsers.get(1);

        for (int i = 0; i < sourceItems.size(); i++) {
            Item item = sourceItems.get(i);
            if (i == 0) item.setOwner(user1);
            if (i == 1) item.setOwner(user2);
            if (i == 2) item.setOwner(user2);
            if (i == 3) item.setOwner(user2);
            em.persist(item);
        }
        em.flush();

        for (int i = 0; i < sourceBookings.size(); i++) {
            Booking booking = sourceBookings.get(i);
            if (i == 0) {
                booking.setBooker(user2);
                booking.setItem(sourceItems.get(0));
            }
            if (i == 1) {
                booking.setBooker(user1);
                booking.setItem(sourceItems.get(1));
            }
            if (i == 2) {
                booking.setBooker(user1);
                booking.setItem(sourceItems.get(2));
            }
            if (i == 3) {
                booking.setBooker(user1);
                booking.setItem(sourceItems.get(3));
            }
            em.persist(booking);
        }
        em.flush();

        try {
            TimeUnit time = TimeUnit.SECONDS;
            time.sleep(stopSec);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void approveBooking() {
        Booking booking1 = sourceBookings.get(0);
        User user1 = sourceUsers.get(0);
        service.approveBooking(booking1.getId(), user1, true);

        Booking booking = em.find(Booking.class, booking1.getId());
        assertThat(booking.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getBooking() {
        Booking booking1 = sourceBookings.get(0);
        User user1 = sourceUsers.get(0);
        Booking booking = service.getBooking(booking1.getId(), user1);

        assertThat(booking.getId(), notNullValue());
        assertThat(booking.getStart(), equalTo(booking1.getStart()));
        assertThat(booking.getEnd(), equalTo(booking1.getEnd()));
        assertThat(booking.getItem(), equalTo(booking1.getItem()));
        assertThat(booking.getBooker(), equalTo(booking1.getBooker()));
        assertThat(booking.getStatus(), equalTo(booking1.getStatus()));
    }

    @Test
    void getUserBooking() {
        User user1 = sourceUsers.get(0);
        List<Booking> targetBookings = service.getUserBooking(user1, BookingRequestStates.CURRENT, 0, 10);
        assertThat(targetBookings, hasSize(1));
    }

    @Test
    void getUserItemsBookings() {
        User user2 = sourceUsers.get(1);
        List<Booking> targetBookings = service.getUserItemsBookings(user2, BookingRequestStates.PAST, 0, 10);
        assertThat(targetBookings, hasSize(1));
    }

    @Test
    void getItemBookings() {
        Item item1 = sourceItems.get(0);
        List<Booking> targetBookings = service.getItemBookings(item1);
        assertThat(targetBookings, hasSize(1));
    }

}
