package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingMapper {
    private final UserService userService;
    private final ItemService itemService;
    private final Validator validator;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static BookingDto toDto(Booking entity) {
        ItemDto itemDto = ItemMapper.toDto(entity.getItem());
        UserDto bookerDto = UserMapper.toDto(entity.getBooker());
        return BookingDto.builder()
                .id(entity.getId())
                .start(entity.getStart().format(DATE_FORMAT))
                .end(entity.getEnd().format(DATE_FORMAT))
                .item(itemDto)
                .booker(bookerDto)
                .status(entity.getStatus().name())
                .build();
    }

    public static BookingShortDto toShortDto(Booking entity) {
        return BookingShortDto.builder()
                .id(entity.getId())
                .start(entity.getStart().format(DATE_FORMAT))
                .end(entity.getEnd().format(DATE_FORMAT))
                .itemId(entity.getItem().getId())
                .bookerId(entity.getBooker().getId())
                .status(entity.getStatus().name())
                .build();
    }

    public Booking toEntity(BookingShortDto bookingShortDto, Long bookerId) {
        Booking booking = Booking.builder()
                .id(bookingShortDto.getId())
                .status(BookingStatus.WAITING)
                .build();

        if (bookingShortDto.getStart() != null && !bookingShortDto.getStart().isEmpty() && !bookingShortDto.getStart().isBlank()) {
            booking.setStart(LocalDateTime.parse(bookingShortDto.getStart(), DATE_FORMAT));
        }
        if (bookingShortDto.getEnd() != null && !bookingShortDto.getEnd().isEmpty() && !bookingShortDto.getEnd().isBlank()) {
            booking.setEnd(LocalDateTime.parse(bookingShortDto.getEnd(), DATE_FORMAT));
        }

        if (bookingShortDto.getStatus() != null) {
            booking.setStatus(BookingStatus.valueOf(bookingShortDto.getStatus()));
        }

        Item item = itemService.getItem(bookingShortDto.getItemId());
        if (item.getOwner().getId() == bookerId) {
            throw new NotFoundException(String.format("Нельзя сделать бронирование на собственную вещь. Вещь %d пренаджежит пользователю %d", bookingShortDto.getItemId(), bookerId));
        }
        if (!item.getAvailable()) {
            throw new ValidationException(String.format("Сделать бронирование возможно только на доступные вещи. Вещь %d не доступна", bookingShortDto.getItemId()));
        }
        booking.setItem(item);

        User user = userService.getUser(bookerId);
        booking.setBooker(user);

        Set<ConstraintViolation<Booking>> violations = validator.validate(booking);
        if (!violations.isEmpty()) {
            for (ConstraintViolation<Booking> validation: violations) {
                throw new ValidationException(validation.getMessage());
            }
        }

        if (booking.getEnd().isBefore(booking.getStart()) ||
                booking.getEnd().equals(booking.getStart())) {
            throw new ValidationException(String.format("Дата начала %s должна быть меньше даты окончания %s", booking.getStart(), booking.getEnd()));
        }

        return booking;
    }
}
