package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ItemRequestMapper {

    public ItemRequestDto toDto(ItemRequest request, List<ItemResponseDto> items) {
        if (request == null) {
            return null;
        }

        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                items
        );
    }

    public ItemRequest toEntity(ItemRequestDto dto, User requestor) {
        if (dto == null) {
            return null;
        }

        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        return request;
    }
}