package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemClient;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient client;

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /items - получение вещей пользователя {}", userId);
        return client.getItemsByOwner(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long itemId) {
        log.info("GET /items/{} - получение вещи пользователем {}", itemId, userId);
        return client.getItemById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @Valid @RequestBody ItemDto itemDto) {
        log.info("POST /items - создание вещи пользователем {}", userId);
        return client.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        log.info("PATCH /items/{} - обновление вещи пользователем {}", itemId, userId);
        return client.updateItem(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId) {
        log.info("DELETE /items/{} - удаление вещи пользователем {}", itemId, userId);
        return client.deleteItem(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam String text) {
        log.info("GET /items/search - поиск вещей по тексту '{}' пользователем {}", text, userId);
        return client.searchItems(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        log.info("POST /items/{}/comment - добавление комментария пользователем {}", itemId, userId);
        return client.addComment(userId, itemId, commentDto);
    }
}