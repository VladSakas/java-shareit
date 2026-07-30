package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    List<Item> findByOwner(Long ownerId);

    Optional<Item> findById(Long id);

    Item save(Item item);

    void deleteById(Long id);

    List<Item> search(String text);
}