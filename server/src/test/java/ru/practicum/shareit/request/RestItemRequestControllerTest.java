package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@Import(ItemRequestMapper.class)
public class RestItemRequestControllerTest {
    @MockBean
    private UserService userService;
    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final User user = User.builder().id(1L).name("test1").email("test1@test.ru").build();
    private final List<ItemRequest> sourceItems = List.of(
            ItemRequest.builder()
                    .id(1L)
                    .description("Хочу вещь 1")
                    .requestor(user)
                    .created(LocalDateTime.now())
                    .build()
    );

    @BeforeEach
    void setUp() {
        when(userService.getUser(anyLong()))
                .thenReturn(user);
    }

    @Test
    void createRequest() throws Exception {
        ItemRequestDto itemRequestDto1 = ItemRequestDto.builder()
                .description("Хочу вещь 1")
                .requestorId(1L)
                .build();
        ItemRequestDto itemRequestDto2 = ItemRequestDto.builder()
                .description("")
                .requestorId(1L)
                .build();

        int userIndex = 0;
        when(itemRequestService.createRequest(any()))
                .thenReturn(sourceItems.get(userIndex));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue(), Long.class))
                .andExpect(jsonPath("$.description", is(sourceItems.get(userIndex).getDescription())))
                .andExpect(jsonPath("$.requestorId", is(sourceItems.get(userIndex).getRequestor().getId()), Long.class))
                .andExpect(jsonPath("$.created", notNullValue()))
                .andExpect(jsonPath("$.items", empty()));
    }

    @Test
    void getUserRequests() throws Exception {
        when(itemRequestService.getUserRequests(any()))
                .thenReturn(sourceItems);

        mvc.perform(get("/requests").header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(sourceItems.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(sourceItems.get(0).getDescription())))
                .andExpect(jsonPath("$[0].requestorId", is(sourceItems.get(0).getRequestor().getId()), Long.class));
    }

    @Test
    void getOtherRequests() throws Exception {
        when(itemRequestService.getOtherRequests(any(), anyInt(), anyInt()))
                .thenReturn(sourceItems);

        mvc.perform(get("/requests/all").header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(sourceItems.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(sourceItems.get(0).getDescription())))
                .andExpect(jsonPath("$[0].requestorId", is(sourceItems.get(0).getRequestor().getId()), Long.class));
    }

    @Test
    void getItemRequest() throws Exception {
        int userIndex = 0;
        when(itemRequestService.getRequest(anyLong()))
                .thenReturn(sourceItems.get(userIndex));

        mvc.perform(get("/requests/1").header("X-Sharer-User-Id", 1))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(sourceItems.get(userIndex).getId()), Long.class))
                .andExpect(jsonPath("$.description", is(sourceItems.get(userIndex).getDescription())))
                .andExpect(jsonPath("$.requestorId", is(sourceItems.get(userIndex).getRequestor().getId()), Long.class));
    }
}
