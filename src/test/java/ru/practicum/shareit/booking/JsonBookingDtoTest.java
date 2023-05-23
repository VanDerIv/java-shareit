package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
public class JsonBookingDtoTest {
    private final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void testBookingDto() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("test item 1")
                .description("test item 1")
                .available(true)
                .build();

        UserDto userDto = UserDto.builder().id(1L).name("test1").email("test1@test.ru").build();

        BookingDto dto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.of(2023, 5, 19, 10, 0, 0).format(DATE_FORMAT))
                .end(LocalDateTime.of(2023, 5, 19, 12, 0, 0).format(DATE_FORMAT))
                .item(itemDto)
                .booker(userDto)
                .status(BookingStatus.WAITING.name())
                .build();

        JsonContent<BookingDto> result = json.write(dto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-05-19T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-05-19T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("test item 1");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("test1");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}
