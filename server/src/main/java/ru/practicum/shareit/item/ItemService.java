package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsByOwner(Long userId);

    ItemDto addNewItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    Item getItemById(Long id);

    void deleteItem(Long userId, Long itemId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);

    ItemDto getItemWithComments(Long userId, Long itemId);

    List<ItemBookingDto> getItemsWithBookingAndComments(Long userId);
}