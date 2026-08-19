package ru.practicum.shareit.item;

import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item, List<Comment> comments, LocalDateTime lastBooking, LocalDateTime nextBooking) {
        if (item == null) {
            return null;
        }

        List<CommentDto> commentDtos = comments != null
                ? comments.stream().map(CommentMapper::toDto).collect(Collectors.toList())
                : List.of();

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getOwner() != null ? item.getOwner().getId() : null,
                item.getRequestId(),
                commentDtos,
                lastBooking,
                nextBooking
        );
    }

    public static ItemDto toItemDto(Item item, List<Comment> comments) {
        return toItemDto(item, comments, null, null);
    }

    public static ItemDto toItemDto(Item item) {
        return toItemDto(item, List.of(), null, null);
    }

    public static Item toItem(ItemDto dto, User owner) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setOwner(owner);
        item.setRequestId(dto.getRequestId());
        return item;
    }
}