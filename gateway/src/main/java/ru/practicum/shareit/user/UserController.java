package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.UserClient;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient client;

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable long id,
                                             @RequestBody UserDto userDto) {
        log.info("PATCH /users/{} - обновление пользователя", id);
        return client.updateUser(id, userDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable long id) {
        log.info("GET /users/{} - получение пользователя", id);
        return client.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable long id) {
        log.info("DELETE /users/{} - удаление пользователя", id);
        return client.deleteUser(id);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("GET /users - получение всех пользователей");
        return client.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("POST /users - создание пользователя");
        return client.createUser(userDto);
    }
}