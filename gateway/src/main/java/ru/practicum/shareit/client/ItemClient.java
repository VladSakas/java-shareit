package ru.practicum.shareit.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.item.CommentDto;
import ru.practicum.shareit.item.ItemDto;

@Slf4j
@Service
public class ItemClient extends BaseClient {

    @Value("${shareit-server.url}")
    private String serverUrl;

    public ItemClient(RestTemplate rest) {
        super(rest);
    }

    public ResponseEntity<Object> getItemsByOwner(Long userId) {
        String path = serverUrl + "/items";
        log.info("GET {}", path);
        return get(path, userId);
    }

    public ResponseEntity<Object> getItemById(Long userId, Long itemId) {
        String path = serverUrl + "/items/" + itemId;
        log.info("GET {}", path);
        return get(path, userId);
    }

    public ResponseEntity<Object> createItem(Long userId, ItemDto itemDto) {
        String path = serverUrl + "/items";
        log.info("POST {}", path);
        return post(path, userId, itemDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto itemDto) {
        itemDto.setId(itemId);
        String path = serverUrl + "/items/" + itemId;
        log.info("PATCH {}", path);
        return patch(path, userId, itemDto);
    }

    public ResponseEntity<Object> deleteItem(Long userId, Long itemId) {
        String path = serverUrl + "/items/" + itemId;
        log.info("DELETE {}", path);
        return delete(path, userId);
    }

    public ResponseEntity<Object> searchItems(Long userId, String text) {
        String path = serverUrl + "/items/search?text=" + text;
        log.info("GET {}", path);
        return get(path, userId);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto commentDto) {
        String path = serverUrl + "/items/" + itemId + "/comment";
        log.info("POST {}", path);
        return post(path, userId, commentDto);
    }
}