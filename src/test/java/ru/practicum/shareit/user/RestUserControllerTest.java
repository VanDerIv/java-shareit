package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import(UserMapper.class)
public class RestUserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final List<User> sourceUsers = List.of(
            User.builder().id(1L).name("test1").email("test1@test.ru").build(),
            User.builder().id(2L).name("test2").email("test2@test.ru").build(),
            User.builder().id(3L).name("test3").email("test3@test.ru").build()
    );

    @Test
    void getUsers() throws Exception {
        when(userService.getUsers())
                .thenReturn(sourceUsers);

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(sourceUsers.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(sourceUsers.get(0).getName())))
                .andExpect(jsonPath("$[0].email", is(sourceUsers.get(0).getEmail())));
    }

    @Test
    void getUser() throws Exception {
        int userIndex = 0;
        when(userService.getUser(anyLong()))
                .thenReturn(sourceUsers.get(userIndex));

        mvc.perform(get("/users/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(sourceUsers.get(userIndex).getId()), Long.class))
                .andExpect(jsonPath("$.name", is(sourceUsers.get(userIndex).getName())))
                .andExpect(jsonPath("$.email", is(sourceUsers.get(userIndex).getEmail())));
    }

    @Test
    void createUser() throws Exception {
        UserDto userDto1 = UserDto.builder().id(1L).name("test1").email("test1@test.ru").build();
        UserDto userDto2 = UserDto.builder().id(1L).name("test1").email("").build();
        UserDto userDto3 = UserDto.builder().id(1L).email("test1@test.ru").build();
        int userIndex = 0;

        when(userService.createUser(any()))
                .thenReturn(sourceUsers.get(userIndex));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue(), Long.class))
                .andExpect(jsonPath("$.name", is(sourceUsers.get(userIndex).getName())))
                .andExpect(jsonPath("$.email", is(sourceUsers.get(userIndex).getEmail())));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(400, 500))));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto3))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(400, 500))));
    }

    @Test
    void patchUser() throws Exception {
        int userIndex = 0;
        when(userService.getUser(anyLong()))
                .thenReturn(sourceUsers.get(userIndex));

        UserDto userDto1 = UserDto.builder().name("test11").build();
        UserDto userDto2 = UserDto.builder().email("test1@test.ru").build();
        UserDto userDto3 = UserDto.builder().name("").build();
        UserDto userDto4 = UserDto.builder().email("").build();

        when(userService.updateUser(any(), anyLong()))
                .thenReturn(sourceUsers.get(userIndex));

        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue(), Long.class))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.email", notNullValue()));

        mvc.perform(patch("/users/2")
                        .content(mapper.writeValueAsString(userDto2))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue(), Long.class))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.email", notNullValue()));

        mvc.perform(patch("/users/3")
                        .content(mapper.writeValueAsString(userDto3))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(400, 500))));

        mvc.perform(patch("/users/4")
                        .content(mapper.writeValueAsString(userDto4))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(400, 500))));

        mvc.perform(patch("/users/t")
                        .content(mapper.writeValueAsString(userDto1))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().is(in(List.of(400, 500))));
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk());
    }
}
