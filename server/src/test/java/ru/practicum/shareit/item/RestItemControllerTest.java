package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.CommentShortDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@Import({ItemMapper.class, CommentMapper.class})
public class RestItemControllerTest {
    @MockBean
    private ItemService itemService;
    @MockBean
    private UserService userService;
    @MockBean
    private ItemRequestService itemRequestService;
    @MockBean
    private CommentService commentService;
    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

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
    private final List<Comment> sourceComments = List.of(
            Comment.builder()
                    .id(1L)
                    .text("comment to item 1")
                    .item(sourceItems.get(0))
                    .author(user2)
                    .created(LocalDateTime.now())
                    .build(),
            Comment.builder()
                    .id(2L)
                    .text("comment to item 2 from user 1")
                    .item(sourceItems.get(1))
                    .author(user1)
                    .created(LocalDateTime.now())
                    .build(),
            Comment.builder()
                    .id(3L)
                    .text("comment to item 2 from user 3")
                    .item(sourceItems.get(1))
                    .author(user3)
                    .created(LocalDateTime.now())
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
    ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("Хочу вещь 1")
            .requestor(user2)
            .created(LocalDateTime.now())
            .build();

    @BeforeEach
    void setUp() {
        when(userService.getUser(1L))
                .thenReturn(user1);
        when(userService.getUser(2L))
                .thenReturn(user2);
        when(userService.getUser(3L))
                .thenReturn(user3);

        when(commentService.getItemComments(any()))
                .thenReturn(sourceComments);
        when(bookingService.getItemBookings(any()))
                .thenReturn(sourceBookings);

        when(itemService.getItem(1L))
                .thenReturn(sourceItems.get(0));
        when(itemService.getItem(2L))
                .thenReturn(sourceItems.get(1));
    }

    @Test
    void getUserItems() throws Exception {
        when(itemService.getUserItems(any(), anyInt(), anyInt()))
                .thenReturn(sourceItems);

        mvc.perform(get("/items").header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(sourceItems.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(sourceItems.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(sourceItems.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(sourceItems.get(0).getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking", nullValue()))
                .andExpect(jsonPath("$[0].nextBooking", notNullValue()))
                .andExpect(jsonPath("$[0].nextBooking.id", is(sourceBookings.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[0].comments", notNullValue()))
                .andExpect(jsonPath("$[0].comments", hasSize(3)))
                .andExpect(jsonPath("$[0].comments[0].id", is(sourceComments.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].requestId", nullValue()));

        mvc.perform(get("/items").header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void findAvailableItems() throws Exception {
        when(itemService.findAvailableItems(any(), anyString(), anyInt(), anyInt()))
                .thenReturn(sourceItems);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "123"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(sourceItems.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(sourceItems.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(sourceItems.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(sourceItems.get(0).getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking", nullValue()))
                .andExpect(jsonPath("$[0].nextBooking", nullValue()))
                .andExpect(jsonPath("$[0].comments", empty()))
                .andExpect(jsonPath("$[0].requestId", nullValue()));

        mvc.perform(get("/items/search").header("X-Sharer-User-Id", 1)
                        .param("text", "123")
                        .param("from", "0")
                        .param("size", "1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void getItem() throws Exception {
        mvc.perform(get("/items/1").header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(sourceItems.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.name", is(sourceItems.get(0).getName())))
                .andExpect(jsonPath("$.description", is(sourceItems.get(0).getDescription())))
                .andExpect(jsonPath("$.available", is(sourceItems.get(0).getAvailable())))
                .andExpect(jsonPath("$.lastBooking", nullValue()))
                .andExpect(jsonPath("$.nextBooking", notNullValue()))
                .andExpect(jsonPath("$.nextBooking.id", is(sourceBookings.get(1).getId()), Long.class))
                .andExpect(jsonPath("$.comments", notNullValue()))
                .andExpect(jsonPath("$.comments", hasSize(3)))
                .andExpect(jsonPath("$.comments[0].id", is(sourceComments.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.requestId", nullValue()));

        mvc.perform(get("/items/2").header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(sourceItems.get(1).getId()), Long.class))
                .andExpect(jsonPath("$.name", is(sourceItems.get(1).getName())))
                .andExpect(jsonPath("$.description", is(sourceItems.get(1).getDescription())))
                .andExpect(jsonPath("$.available", is(sourceItems.get(1).getAvailable())))
                .andExpect(jsonPath("$.lastBooking", nullValue()))
                .andExpect(jsonPath("$.nextBooking", nullValue()))
                .andExpect(jsonPath("$.comments", notNullValue()))
                .andExpect(jsonPath("$.comments", hasSize(3)))
                .andExpect(jsonPath("$.comments[0].id", is(sourceComments.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.requestId", nullValue()));
    }

    @Test
    void createItem() throws Exception {
        ItemDto itemDto1 = ItemDto.builder()
                .name("test item 1")
                .description("test item 1")
                .available(true)
                .build();
        ItemDto itemDto2 = ItemDto.builder()
                .description("test item 1")
                .available(true)
                .build();
        ItemDto itemDto3 = ItemDto.builder()
                .name("test item 1")
                .description("")
                .available(false)
                .build();
        ItemDto itemDto4 = ItemDto.builder()
                .name("test item 1")
                .description("test item 1")
                .build();

        when(itemService.createItem(any()))
                .thenReturn(sourceItems.get(0));
        when(itemRequestService.getRequest(anyLong()))
                .thenReturn(itemRequest);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue(), Long.class))
                .andExpect(jsonPath("$.name", is(sourceItems.get(0).getName())))
                .andExpect(jsonPath("$.description", is(sourceItems.get(0).getDescription())))
                .andExpect(jsonPath("$.available", is(sourceItems.get(0).getAvailable())))
                .andExpect(jsonPath("$.lastBooking", nullValue()))
                .andExpect(jsonPath("$.nextBooking", nullValue()))
                .andExpect(jsonPath("$.comments", empty()))
                .andExpect(jsonPath("$.requestId", nullValue()));
    }

    @Test
    void patchItem() throws Exception {
        ItemDto itemDto1 = ItemDto.builder()
                .name("test item 1111")
                .build();
        ItemDto itemDto2 = ItemDto.builder()
                .description("test item 1111")
                .build();
        ItemDto itemDto3 = ItemDto.builder()
                .available(false)
                .build();
        ItemDto itemDto4 = ItemDto.builder()
                .name("")
                .build();

        when(itemService.updateItem(any()))
                .then(AdditionalAnswers.returnsFirstArg());

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue(), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto1.getName())))
                .andExpect(jsonPath("$.description", is(sourceItems.get(0).getDescription())))
                .andExpect(jsonPath("$.available", is(sourceItems.get(0).getAvailable())));

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue(), Long.class))
                .andExpect(jsonPath("$.name", is(sourceItems.get(0).getName())))
                .andExpect(jsonPath("$.description", is(itemDto2.getDescription())))
                .andExpect(jsonPath("$.available", is(sourceItems.get(0).getAvailable())));

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto3))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue(), Long.class))
                .andExpect(jsonPath("$.name", is(sourceItems.get(0).getName())))
                .andExpect(jsonPath("$.description", is(sourceItems.get(0).getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto3.getAvailable())));

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto4))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(400, 500))));
    }

    @Test
    void deleteItem() throws Exception {
        mvc.perform(delete("/items/1").header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }

    @Test
    void createItemComment() throws Exception {
        CommentShortDto commentShortDto1 = CommentShortDto.builder()
                .text("comment to item 1")
                .itemId(1L)
                .authorName("I")
                .build();
        CommentShortDto commentShortDto2 = CommentShortDto.builder()
                .itemId(2L)
                .authorName("You")
                .build();

        when(commentService.createComment(any()))
                .thenReturn(sourceComments.get(0));
        when(commentService.getComment(1L))
                .thenReturn(sourceComments.get(0));
        when(commentService.getComment(2L))
                .thenReturn(sourceComments.get(1));

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentShortDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue(), Long.class))
                .andExpect(jsonPath("$.text", is(sourceComments.get(0).getText())))
                .andExpect(jsonPath("$.authorName", is(sourceComments.get(0).getAuthor().getName())))
                .andExpect(jsonPath("$.created", notNullValue()))
                .andExpect(jsonPath("$.item", notNullValue()))
                .andExpect(jsonPath("$.item.id", is(1L), Long.class));
    }

}
