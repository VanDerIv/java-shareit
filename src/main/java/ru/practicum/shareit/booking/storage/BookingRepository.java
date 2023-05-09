package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByItem(Item item);

    @Query("select (count(b) > 0) from Booking b " +
            "where b.item = ?1 and b.booker = ?2 and b.status <> 'REJECTED' and b.start <= ?3")
    boolean existsByItemAndBookerAndStatusNotAndStartLessThanEqual(Item item, User booker, LocalDateTime start);

    List<Booking> findByBooker(User user);

    List<Booking> findByItem_Owner(User user);

}
