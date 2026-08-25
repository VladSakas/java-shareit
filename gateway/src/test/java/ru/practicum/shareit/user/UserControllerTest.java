package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.UserClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void getAllUsers_shouldReturnUsers() throws Exception {
        String mockResponse = "[{\"id\":1,\"name\":\"Test User\",\"email\":\"test@user.com\"}]";

        when(userClient.getAllUsers())
                .thenReturn(ResponseEntity.ok(mockResponse));

        mockMvc.perform(get("/users")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("New User");
        userDto.setEmail("new@user.com");

        String mockResponse = "{\"id\":1,\"name\":\"New User\",\"email\":\"new@user.com\"}";

        when(userClient.createUser(any(UserDto.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setName("Updated User");
        userDto.setEmail("updated@user.com");

        String mockResponse = "{\"id\":1,\"name\":\"Updated User\",\"email\":\"updated@user.com\"}";

        when(userClient.updateUser(anyLong(), any(UserDto.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        mockMvc.perform(patch("/users/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }

    @Test
    void deleteUser_shouldReturnOk() throws Exception {
        when(userClient.deleteUser(anyLong()))
                .thenReturn(ResponseEntity.ok("{}"));

        mockMvc.perform(delete("/users/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }
}