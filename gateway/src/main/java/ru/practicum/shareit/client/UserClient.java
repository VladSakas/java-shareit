package ru.practicum.shareit.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserDto;

@Slf4j
@Service
public class UserClient extends BaseClient {

    public UserClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(
                builder
                .rootUri(serverUrl)
                .build()
        );
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        log.info("POST /users");
        return post("/users", userDto);
    }

    public ResponseEntity<Object> updateUser(Long userId, UserDto userDto) {
        log.info("PATCH /users/{}", userId);
        userDto.setId(userId);
        return patch("/users/" + userId, userId, userDto);
    }

    public ResponseEntity<Object> getUserById(Long userId) {
        log.info("GET /users/{}", userId);
        return get("/users/" + userId, userId);
    }

    public ResponseEntity<Object> getAllUsers() {
        log.info("GET /users");
        return get("/users");
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        log.info("DELETE /users/{}", userId);
        return delete("/users/" + userId, userId);
    }


}