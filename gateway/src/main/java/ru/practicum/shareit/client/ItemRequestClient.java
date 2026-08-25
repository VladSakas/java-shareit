package ru.practicum.shareit.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.request.ItemRequestDto;

@Slf4j
@Service
public class ItemRequestClient extends BaseClient {

    @Value("${shareit-server.url}")
    private String serverUrl;

    public ItemRequestClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> createRequest(Long userId, ItemRequestDto requestDto) {
        String path = serverUrl + "/requests";
        log.info("POST {}", path);
        return post(path, userId, requestDto);
    }

    public ResponseEntity<Object> getRequestsByUser(Long userId) {
        String path = serverUrl + "/requests";
        log.info("GET {}", path);
        return get(path, userId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId) {
        String path = serverUrl + "/requests/all";
        log.info("GET {}", path);
        return get(path, userId);
    }

    public ResponseEntity<Object> getRequestById(Long userId, Long requestId) {
        String path = serverUrl + "/requests/" + requestId;
        log.info("GET {}", path);
        return get(path, userId);
    }
}