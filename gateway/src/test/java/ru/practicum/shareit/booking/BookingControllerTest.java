package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.BookingClient;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingClient bookingClient;

    @Test
    void createBooking_shouldCreateBooking() throws Exception {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(1L);
        requestDto.setStart(LocalDateTime.now().plusDays(1));
        requestDto.setEnd(LocalDateTime.now().plusDays(2));

        String mockResponse = "{\"id\":1,\"status\":\"WAITING\"}";

        when(bookingClient.createBooking(anyLong(), any(BookingRequestDto.class)))
                .thenReturn(ResponseEntity.ok(mockResponse));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }

    @Test
    void updateBookingStatus_shouldUpdateStatus() throws Exception {
        String mockResponse = "{\"id\":1,\"status\":\"APPROVED\"}";

        when(bookingClient.updateBookingStatus(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok(mockResponse));

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }

    @Test
    void getBooking_shouldReturnBooking() throws Exception {
        String mockResponse = "{\"id\":1,\"status\":\"WAITING\"}";

        when(bookingClient.getBooking(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok(mockResponse));

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }

    @Test
    void getBookings_shouldReturnBookings() throws Exception {
        String mockResponse = "[{\"id\":1,\"status\":\"WAITING\"}]";

        when(bookingClient.getBookingsByUser(anyLong(), anyString()))
                .thenReturn(ResponseEntity.ok(mockResponse));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }

    @Test
    void getBookingsByOwner_shouldReturnBookings() throws Exception {
        String mockResponse = "[{\"id\":1,\"status\":\"WAITING\"}]";

        when(bookingClient.getBookingsByOwner(anyLong(), anyString()))
                .thenReturn(ResponseEntity.ok(mockResponse));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(content().json(mockResponse));
    }
}