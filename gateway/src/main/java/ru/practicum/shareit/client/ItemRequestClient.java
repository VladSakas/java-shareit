package ru.practicum.shareit.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.ItemRequestDto;

@Slf4j
@Service
public class ItemRequestClient extends BaseClient {

    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl,
                             RestTemplateBuilder builder) {
        super(
                builder
                        .rootUri(serverUrl)
                        .build()
        );
    }

    public ResponseEntity<Object> createRequest(Long userId, ItemRequestDto requestDto) {
        log.info("POST /requests");
        return post("/requests", userId, requestDto);
    }

    public ResponseEntity<Object> getRequestsByUser(Long userId) {
        log.info("GET /requests");
        return get("/requests", userId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId) {
        log.info("GET /requests/all");
        return get("/requests/all", userId);
    }

    public ResponseEntity<Object> getRequestById(Long userId, Long requestId) {
        log.info("GET /requests/{}", requestId);
        return get("/requests/" + requestId, userId);
    }
}