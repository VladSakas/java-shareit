package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldSerializeBookingRequestDto() throws Exception {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.of(2026, 8, 25, 10, 0));
        dto.setEnd(LocalDateTime.of(2026, 8, 26, 10, 0));

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"itemId\":1");
        assertThat(json).contains("\"start\":\"2026-08-25T10:00:00\"");
        assertThat(json).contains("\"end\":\"2026-08-26T10:00:00\"");
    }

    @Test
    void shouldDeserializeBookingRequestDto() throws Exception {
        String json = """
                {
                    "itemId": 1,
                    "start": "2026-08-25T10:00:00",
                    "end": "2026-08-26T10:00:00"
                }
                """;

        BookingRequestDto dto = objectMapper.readValue(json, BookingRequestDto.class);

        assertThat(dto.getItemId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 8, 25, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2026, 8, 26, 10, 0));
    }

    @Test
    void shouldSerializeBookingDto() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.of(2026, 8, 25, 10, 0));
        dto.setEnd(LocalDateTime.of(2026, 8, 26, 10, 0));
        dto.setStatus(BookingStatus.WAITING);

        String json = objectMapper.writeValueAsString(dto);

        assertThat(json).contains("\"id\":1");
        assertThat(json).contains("\"start\":\"2026-08-25T10:00:00\"");
        assertThat(json).contains("\"end\":\"2026-08-26T10:00:00\"");
        assertThat(json).contains("\"status\":\"WAITING\"");
    }

    @Test
    void shouldDeserializeBookingDto() throws Exception {
        String json = """
                {
                    "id": 1,
                    "start": "2026-08-25T10:00:00",
                    "end": "2026-08-26T10:00:00",
                    "status": "WAITING"
                }
                """;

        BookingDto dto = objectMapper.readValue(json, BookingDto.class);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2026, 8, 25, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2026, 8, 26, 10, 0));
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.WAITING);
    }
}