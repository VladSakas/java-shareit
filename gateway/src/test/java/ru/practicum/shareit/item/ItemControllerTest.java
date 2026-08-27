package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.ItemClient;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemClient itemClient;

    @Test
    void getItems_shouldReturnItems() throws Exception {
        String mockResponse = "[{\"id\":1,\"name\":\"Test Item\",\"description\":\"Test Desc\",\"available\":true}]";

        when(itemClient.getItemsByOwner(anyLong()))
                .thenReturn(ResponseEntity.ok(mockResponse));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }

    @Test
    void getItemById_shouldReturnItem() throws Exception {
        String mockResponse = "{\"id\":1,\"name\":\"Test Item\",\"description\":\"Test Desc\",\"available\":true}";

        when(itemClient.getItemById(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(mockResponse));

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }

    @Test
    void addItem_shouldCreateItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("New Item");
        itemDto.setDescription("New Desc");
        itemDto.setAvailable(true);

        String mockResponse = "{\"id\":1,\"name\":\"New Item\",\"description\":\"New Desc\",\"available\":true}";

        when(itemClient.createItem(anyLong(), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }

    @Test
    void updateItem_shouldUpdateItem() throws Exception {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");
        itemDto.setDescription("Updated Desc");
        itemDto.setAvailable(false);

        String mockResponse = "{\"id\":1,\"name\":\"Updated Item\",\"description\":\"Updated Desc\",\"available\":false}";

        when(itemClient.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }

    @Test
    void deleteItem_shouldReturnOk() throws Exception {
        when(itemClient.deleteItem(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok("{}"));

        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void searchItems_shouldReturnItems() throws Exception {
        String mockResponse = "[{\"id\":1,\"name\":\"Test Item\",\"description\":\"Test Desc\",\"available\":true}]";

        when(itemClient.searchItems(anyLong(), anyString()))
                .thenReturn(ResponseEntity.ok(mockResponse));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }
}