package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
public class JpaBookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository repository;

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
                    .start(LocalDateTime.now().plusMinutes(1))
                    .end(LocalDateTime.now().plusDays(2))
                    .status(BookingStatus.WAITING)
                    .build(),
            Booking.builder()
                    .start(LocalDateTime.now().plusMinutes(1))
                    .end(LocalDateTime.now().plusDays(1))
                    .status(BookingStatus.WAITING)
                    .build(),
            Booking.builder()
                    .start(LocalDateTime.now().plusMinutes(1))
                    .end(LocalDateTime.now().plusMinutes(2))
                    .status(BookingStatus.REJECTED)
                    .build(),
            Booking.builder()
                    .start(LocalDateTime.now().plusDays(1))
                    .end(LocalDateTime.now().plusDays(2))
                    .status(BookingStatus.WAITING)
                    .build()
    );

    @Test
    public void existsByItemAndBookerAndStatusNotAndStartLessThanEqual() {
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
        Item item1 = sourceItems.get(0);
        Item item2 = sourceItems.get(1);
        Item item3 = sourceItems.get(2);
        Item item4 = sourceItems.get(3);

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

        boolean exist = repository.existsByItemAndBookerAndStatusNotAndStartLessThanEqual(item1, user1,
                LocalDateTime.now().plusMinutes(10));
        assertThat(exist, equalTo(false));

        exist = repository.existsByItemAndBookerAndStatusNotAndStartLessThanEqual(item1, user2,
                LocalDateTime.now().plusMinutes(10));
        assertThat(exist, equalTo(true));

        exist = repository.existsByItemAndBookerAndStatusNotAndStartLessThanEqual(item1, user2,
                LocalDateTime.now());
        assertThat(exist, equalTo(false));

        exist = repository.existsByItemAndBookerAndStatusNotAndStartLessThanEqual(item3, user1,
                LocalDateTime.now().plusMinutes(10));
        assertThat(exist, equalTo(false));

        exist = repository.existsByItemAndBookerAndStatusNotAndStartLessThanEqual(item4, user1,
                LocalDateTime.now().plusMinutes(10));
        assertThat(exist, equalTo(false));

        exist = repository.existsByItemAndBookerAndStatusNotAndStartLessThanEqual(item2, user1,
                LocalDateTime.now().plusMinutes(10));
        assertThat(exist, equalTo(true));
    }
}
