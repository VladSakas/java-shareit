package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();
    private long nextId = 1;

    @Override
    public List<Item> findByOwner(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner() != null
                        && item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(nextId++);
            items.put(item.getId(), item);
            log.info("Создана вещь с ID: {}", item.getId());
        } else {
            items.put(item.getId(), item);
            log.info("Обновлена вещь с ID: {}", item.getId());
        }
        return item;
    }

    @Override
    public void deleteById(Long id) {
        items.remove(id);
        log.info("Удалена вещь с ID: {}", id);
    }

    @Override
    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        String lowerText = text.toLowerCase();
        return items.values().stream()
                .filter(item -> item.getAvailable() != null && item.getAvailable())
                .filter(item ->
                        (item.getName() != null && item.getName().toLowerCase().contains(lowerText)) ||
                                (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerText))
                )
                .collect(Collectors.toList());
    }
}