package ru.practicum.shareit.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.ItemDto;

@Slf4j
@Service
public class ItemClient extends BaseClient {

    public ItemClient(@Value("${shareit-server.url}") String serverUrl,
                      RestTemplateBuilder builder) {
        super(
                builder
                        .rootUri(serverUrl)
                        .build()
        );
    }

    public ResponseEntity<Object> getItemsByOwner(Long userId) {
        log.info("GET /items");
        return get("/items", userId);
    }

    public ResponseEntity<Object> getItemById(Long userId, Long itemId) {
        log.info("GET /items/{}", itemId);
        return get("/items/" + itemId, userId);
    }

    public ResponseEntity<Object> createItem(Long userId, ItemDto itemDto) {
        log.info("POST /items");
        return post("/items", userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto itemDto) {
        log.info("PATCH /items/{}", itemId);
        itemDto.setId(itemId);
        return patch("/items/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> deleteItem(Long userId, Long itemId) {
        log.info("DELETE /items/{}", itemId);
        return delete("/items/" + itemId, userId);
    }

    public ResponseEntity<Object> searchItems(Long userId, String text) {
        log.info("GET /items/search?text={}", text);
        return get("/items/search?text=" + text, userId);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto commentDto) {
        log.info("POST /items/{}/comment", itemId);
        return post("/items/" + itemId + "/comment", userId, commentDto);
    }
}