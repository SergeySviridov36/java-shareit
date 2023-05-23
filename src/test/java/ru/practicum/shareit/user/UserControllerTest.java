package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;
    private final Long userId = 1L;

    private static final String USER_ID = "X-Sharer-User-Id";

    private final UserDto userDto = new UserDto(
            null,
            "nameUserTest",
            "emailUserTest@mail.ru"
    );

    private final UserDto userDtoResponse = new UserDto(
            1L,
            "nameUserTest",
            "emailUserTest@mail.ru"
    );

    @Test
    void createUserTest() throws Exception {
        when(userService.create(any()))
                .thenReturn(userDtoResponse);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDtoResponse.getId()))
                .andExpect(jsonPath("$.name").value(userDtoResponse.getName()));
        verify(userService, times(1)).create(userDto);
    }

    @Test
    void findUserByIdTest() throws Exception {
        when(userService.findUserById(userId))
                .thenReturn(userDtoResponse);

        mockMvc.perform(get("/users/{userId}", userId)
                        .header(USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDtoResponse.getId()))
                .andExpect(jsonPath("$.name").value(userDtoResponse.getName()));
        verify(userService, times(1)).findUserById(userId);
    }

    @Test
    void updateUserTest() throws Exception {
        when(userService.updateUser(userId, userDto))
                .thenReturn(userDtoResponse);

        mockMvc.perform(patch("/users/{userId}", userId)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDtoResponse.getId()))
                .andExpect(jsonPath("$.name").value(userDtoResponse.getName()));
        verify(userService, times(1)).updateUser(userId, userDto);
    }

    @Test
    void deleteUserTest() throws Exception {
        mockMvc.perform(delete("/users/{userId}", userId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUser(userId);
    }

    @Test
    void findAllUsersTest() throws Exception {
        when(userService.findAllUsers())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(userService, times(1)).findAllUsers();
    }
}
