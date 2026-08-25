package ru.practicum.shareit.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.user.UserDto;

@Slf4j
@Service
public class UserClient {

    private final RestTemplate restTemplate;
    private final String serverUrl;

    public UserClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        this.serverUrl = serverUrl;
        this.restTemplate = builder
                .requestFactory(() -> new JdkClientHttpRequestFactory())
                .build();
    }

    public ResponseEntity<String> createUser(UserDto userDto) {
        log.info("POST /users");
        return exchange("/users", HttpMethod.POST, null, userDto);
    }

    public ResponseEntity<String> updateUser(long userId, UserDto userDto) {
        log.info("PATCH /users/{}", userId);
        return exchange("/users/" + userId, HttpMethod.PATCH, null, userDto);
    }

    public ResponseEntity<String> getUser(long userId) {
        log.info("GET /users/{}", userId);
        return exchange("/users/" + userId, HttpMethod.GET, null, null);
    }

    public ResponseEntity<String> getAllUsers() {
        log.info("GET /users");
        return exchange("/users", HttpMethod.GET, null, null);
    }

    public ResponseEntity<String> deleteUser(long userId) {
        log.info("DELETE /users/{}", userId);
        return exchange("/users/" + userId, HttpMethod.DELETE, null, null);
    }

    private ResponseEntity<String> exchange(String path, HttpMethod method, HttpHeaders headers, Object body) {
        HttpHeaders finalHeaders = new HttpHeaders();
        if (headers != null) {
            finalHeaders.putAll(headers);
        }
        if (!finalHeaders.containsKey(HttpHeaders.CONTENT_TYPE)) {
            finalHeaders.setContentType(MediaType.APPLICATION_JSON);
        }
        HttpEntity<Object> entity = new HttpEntity<>(body, finalHeaders);
        try {
            return restTemplate.exchange(serverUrl + path, method, entity, String.class);
        } catch (HttpStatusCodeException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }
}