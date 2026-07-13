package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsByOwner(Long userId);

    ItemDto getItemById(Long userId, Long itemId);

    ItemDto createItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    void deleteItem(Long userId, Long itemId);

    List<ItemDto> searchItems(String text);
}