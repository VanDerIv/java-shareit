package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.in;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@Import(BookingMapper.class)
public class RestBookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @MockBean
    private UserService userService;
    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final User user1 = User.builder().id(1L).name("test1").email("test1@test.ru").build();
    private final User user2 = User.builder().id(2L).name("test2").email("test2@test.ru").build();
    private final User user3 = User.builder().id(3L).name("test3").email("test3@test.ru").build();
    private final List<Item> sourceItems = List.of(
            Item.builder()
                    .id(1L)
                    .name("test item 1")
                    .description("test item 1")
                    .available(true)
                    .owner(user1)
                    .build(),
            Item.builder()
                    .id(2L)
                    .name("test item 2")
                    .description("test item 2")
                    .available(true)
                    .owner(user2)
                    .build(),
            Item.builder()
                    .id(3L)
                    .name("test item 3")
                    .description("test item 3")
                    .available(false)
                    .owner(user2)
                    .build()
    );
    private final List<Booking> sourceBookings = List.of(
            Booking.builder()
                    .id(1L)
                    .start(LocalDateTime.now().plusMinutes(1))
                    .end(LocalDateTime.now().plusMinutes(10))
                    .item(sourceItems.get(0))
                    .booker(user2)
                    .status(BookingStatus.WAITING)
                    .build(),
            Booking.builder()
                    .id(2L)
                    .start(LocalDateTime.now().plusMinutes(100))
                    .end(LocalDateTime.now().plusMinutes(400))
                    .item(sourceItems.get(1))
                    .booker(user1)
                    .status(BookingStatus.APPROVED)
                    .build(),
            Booking.builder()
                    .id(3L)
                    .start(LocalDateTime.now().minusMinutes(10))
                    .end(LocalDateTime.now().plusMinutes(10))
                    .item(sourceItems.get(2))
                    .booker(user3)
                    .status(BookingStatus.REJECTED)
                    .build()
    );

    @BeforeEach
    void setUp() {
        when(userService.getUser(1L))
                .thenReturn(user1);
        when(userService.getUser(2L))
                .thenReturn(user2);
        when(userService.getUser(3L))
                .thenReturn(user3);

        when(itemService.getItem(1L))
                .thenReturn(sourceItems.get(0));
        when(itemService.getItem(2L))
                .thenReturn(sourceItems.get(1));
        when(itemService.getItem(3L))
                .thenReturn(sourceItems.get(2));
    }

    @Test
    void createBooking() throws Exception {
        BookingShortDto bookingShortDto1 = BookingShortDto.builder()
                .start(LocalDateTime.now().plusMinutes(1).format(DATE_FORMAT))
                .end(LocalDateTime.now().plusMinutes(10).format(DATE_FORMAT))
                .itemId(1L)
                .bookerId(2L)
                .status(BookingStatus.WAITING.name())
                .build();

        when(bookingService.createBooking(any()))
                .thenReturn(sourceBookings.get(0));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(bookingShortDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue(), Long.class))
                .andExpect(jsonPath("$.start", is(sourceBookings.get(0).getStart().format(DATE_FORMAT))))
                .andExpect(jsonPath("$.end", is(sourceBookings.get(0).getEnd().format(DATE_FORMAT))))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.item.id", is(sourceItems.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.booker", notNullValue()))
                .andExpect(jsonPath("$.booker.id", is(user2.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(sourceBookings.get(0).getStatus().name())));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingShortDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(404))));

        BookingShortDto bookingShortDto2 = BookingShortDto.builder()
                .start(LocalDateTime.now().minusMinutes(1).format(DATE_FORMAT))
                .end(LocalDateTime.now().plusMinutes(10).format(DATE_FORMAT))
                .itemId(1L)
                .bookerId(2L)
                .status(BookingStatus.WAITING.name())
                .build();
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(bookingShortDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(400, 500))));

        BookingShortDto bookingShortDto3 = BookingShortDto.builder()
                .start(LocalDateTime.now().plusMinutes(1).format(DATE_FORMAT))
                .end(LocalDateTime.now().plusMinutes(10).format(DATE_FORMAT))
                .itemId(3L)
                .bookerId(2L)
                .status(BookingStatus.WAITING.name())
                .build();
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(bookingShortDto3))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(404))));

        BookingShortDto bookingShortDto4 = BookingShortDto.builder()
                .start(LocalDateTime.now().plusMinutes(100).format(DATE_FORMAT))
                .end(LocalDateTime.now().plusMinutes(10).format(DATE_FORMAT))
                .itemId(1L)
                .bookerId(2L)
                .status(BookingStatus.WAITING.name())
                .build();
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(bookingShortDto4))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(400, 500))));

        BookingShortDto bookingShortDto5 = BookingShortDto.builder()
                .end(LocalDateTime.now().plusMinutes(10).format(DATE_FORMAT))
                .itemId(1L)
                .bookerId(2L)
                .status(BookingStatus.WAITING.name())
                .build();
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(bookingShortDto5))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(400, 500))));

        BookingShortDto bookingShortDto6 = BookingShortDto.builder()
                .start(LocalDateTime.now().plusMinutes(100).format(DATE_FORMAT))
                .itemId(1L)
                .bookerId(2L)
                .status(BookingStatus.WAITING.name())
                .build();
        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(bookingShortDto6))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(400, 500))));
    }

    @Test
    void approveBooking() throws Exception {
        when(bookingService.approveBooking(anyLong(), any(), anyBoolean()))
                .thenReturn(sourceBookings.get(0));

        mvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 2)
                        .param("approved", "true"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue(), Long.class));
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.getBooking(anyLong(), any()))
                .thenReturn(sourceBookings.get(0));

        mvc.perform(get("/bookings/1").header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void getUserBookings() throws Exception {
        when(bookingService.getUserBooking(any(), any(), anyInt(), anyInt()))
                .thenReturn(sourceBookings);

        mvc.perform(get("/bookings").header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[1].id", notNullValue(), Long.class))
                .andExpect(jsonPath("$[1].start", is(sourceBookings.get(1).getStart().format(DATE_FORMAT))))
                .andExpect(jsonPath("$[1].end", is(sourceBookings.get(1).getEnd().format(DATE_FORMAT))))
                .andExpect(jsonPath("$[1].item", notNullValue()))
                .andExpect(jsonPath("$[1].item.id", is(sourceItems.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].booker", notNullValue()))
                .andExpect(jsonPath("$[1].booker.id", is(user1.getId()), Long.class))
                .andExpect(jsonPath("$[1].status", is(sourceBookings.get(1).getStatus().name())));

        mvc.perform(get("/bookings").header("X-Sharer-User-Id", 1)
                        .param("from", "-1")
                        .param("size", "5"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(400, 500))));

        mvc.perform(get("/bookings").header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "0"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(400, 500))));

        mvc.perform(get("/bookings").header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        mvc.perform(get("/bookings").header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "UNKNOWN"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(400, 500))));
    }

    @Test
    void getBookingsUserItems() throws Exception {
        when(bookingService.getUserItemsBookings(any(), any(), anyInt(), anyInt()))
                .thenReturn(sourceBookings);

        mvc.perform(get("/bookings/owner").header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[2].id", notNullValue(), Long.class))
                .andExpect(jsonPath("$[2].start", is(sourceBookings.get(2).getStart().format(DATE_FORMAT))))
                .andExpect(jsonPath("$[2].end", is(sourceBookings.get(2).getEnd().format(DATE_FORMAT))))
                .andExpect(jsonPath("$[2].item", notNullValue()))
                .andExpect(jsonPath("$[2].item.id", is(sourceItems.get(2).getId()), Long.class))
                .andExpect(jsonPath("$[2].booker", notNullValue()))
                .andExpect(jsonPath("$[2].booker.id", is(user3.getId()), Long.class))
                .andExpect(jsonPath("$[2].status", is(sourceBookings.get(2).getStatus().name())));

        mvc.perform(get("/bookings").header("X-Sharer-User-Id", 1)
                        .param("from", "-1")
                        .param("size", "5"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(400, 500))));

        mvc.perform(get("/bookings").header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "0"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(400, 500))));

        mvc.perform(get("/bookings").header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());

        mvc.perform(get("/bookings").header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "1")
                        .param("state", "UNKNOWN"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(400, 500))));
    }
}
