package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.ItemRequestClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Test
    void createRequest_shouldCreateRequest() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need a drill");

        String mockResponse = "{\"id\":1,\"description\":\"Need a drill\",\"created\":\"2026-08-25T01:00:00\"}";

        when(itemRequestClient.createRequest(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }

    @Test
    void getRequestsByUser_shouldReturnRequests() throws Exception {
        String mockResponse = "[{\"id\":1,\"description\":\"Need a drill\",\"created\":\"2026-08-25T01:00:00\"}]";

        when(itemRequestClient.getRequestsByUser(anyLong()))
                .thenReturn(ResponseEntity.ok(mockResponse));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }

    @Test
    void getAllRequests_shouldReturnAllRequests() throws Exception {
        String mockResponse = "[{\"id\":2,\"description\":\"Need a hammer\",\"created\":\"2026-08-25T01:00:00\"}]";

        when(itemRequestClient.getAllRequests(anyLong()))
                .thenReturn(ResponseEntity.ok(mockResponse));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }

    @Test
    void getRequestById_shouldReturnRequest() throws Exception {
        String mockResponse = "{\"id\":1,\"description\":\"Need a drill\",\"created\":\"2026-08-25T01:00:00\"}";

        when(itemRequestClient.getRequestById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(mockResponse));

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }
}