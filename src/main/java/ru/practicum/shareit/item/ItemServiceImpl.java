package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public List<ItemDto> getItemsByOwner(Long userId) {
        log.info("Получен запрос на получение вещей пользователя с id: {}", userId);
        userService.getUserById(userId);
        List<ItemDto> items = itemRepository.findByOwnerId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("Найдено {} вещей у пользователя {}", items.size(), userId);
        return items;
    }

    @Override
    @Transactional
    public ItemDto addNewItem(Long userId, ItemDto itemDto) {
        log.info("Получен запрос на создание вещи пользователем с id: {}", userId);
        User owner = getUserEntity(userId);

        Item item = ItemMapper.toItem(itemDto, owner);

        if (item.getName() == null || item.getName().isBlank()) {
            throw new BadRequestException("Название вещи не может быть пустым");
        }
        if (item.getAvailable() == null) {
            throw new BadRequestException("Статус доступности должен быть указан");
        }

        Item saved = itemRepository.save(item);
        log.info("Создана вещь с id: {} для пользователя {}", saved.getId(), userId);
        return ItemMapper.toItemDto(saved);
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        log.info("Получен запрос на обновление вещи с id: {} пользователем {}", itemId, userId);
        userService.getUserById(userId);
        Item existingItem = getItemById(itemId);

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь не является владельцем этой вещи");
        }

        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }
        log.info("Вещь с id: {} обновлена", itemId);
        return ItemMapper.toItemDto(existingItem);
    }

    @Override
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));
    }

    @Override
    @Transactional
    public void deleteItem(Long userId, Long itemId) {
        log.info("Получен запрос на удаление вещи с id: {} пользователем {}", itemId, userId);
        Item item = getItemById(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь не является владельцем этой вещи");
        }
        itemRepository.deleteById(itemId);
        log.info("Вещь с id: {} удалена", itemId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        log.info("Получен запрос на поиск вещей по тексту: {}", text);
        if (text == null || text.isBlank()) {
            return List.of();
        }
        List<ItemDto> items = itemRepository.search(text.trim()).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        log.info("Найдено {} вещей по запросу '{}'", items.size(), text);
        return items;
    }

    @Override
    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        log.info("Получен запрос на добавление комментария к вещи с id: {} пользователем {}", itemId, userId);
        User author = getUserEntity(userId);
        Item item = getItemById(itemId);

        List<Booking> bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.APPROVED);
        boolean hasCompletedBooking = bookings.stream()
                .anyMatch(b -> b.getItem().getId().equals(itemId) && b.getEnd().isBefore(LocalDateTime.now()));

        if (!hasCompletedBooking) {
            throw new BadRequestException("Пользователь не брал эту вещь в аренду");
        }

        Comment comment = CommentMapper.toEntity(commentDto, item, author);
        Comment saved = commentRepository.save(comment);
        log.info("Комментарий добавлен к вещи с id: {}", itemId);
        return CommentMapper.toDto(saved);
    }

    @Override
    public ItemDto getItemWithComments(Long userId, Long itemId) {
        log.info("Получен запрос на получение вещи с id: {} с комментариями пользователем {}", itemId, userId);
        userService.getUserById(userId);
        Item item = getItemById(itemId);
        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId);

        LocalDateTime lastBooking = null;
        LocalDateTime nextBooking = null;
        LocalDateTime now = LocalDateTime.now();

        if (item.getOwner().getId().equals(userId)) {
            List<Booking> pastBookings = bookingRepository.findPastApprovedByItemId(itemId, now);
            lastBooking = !pastBookings.isEmpty() ? pastBookings.getFirst().getEnd() : null;

            List<Booking> futureBookings = bookingRepository.findFutureApprovedByItemId(itemId, now);
            nextBooking = !futureBookings.isEmpty() ? futureBookings.getFirst().getStart() : null;
        }

        return ItemMapper.toItemDto(item, comments, lastBooking, nextBooking);
    }

    @Override
    public List<ItemBookingDto> getItemsWithBookingAndComments(Long userId) {
        log.info("Получен запрос на получение вещей с бронированиями и комментариями для пользователя {}", userId);
        userService.getUserById(userId);
        List<Item> items = itemRepository.findByOwnerId(userId);
        LocalDateTime now = LocalDateTime.now();

        List<ItemBookingDto> result = items.stream()
                .map(item -> {
                    List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(item.getId());

                    LocalDateTime lastBooking = null;
                    LocalDateTime nextBooking = null;

                    if (item.getOwner().getId().equals(userId)) {
                        List<Booking> pastBookings = bookingRepository.findPastApprovedByItemId(item.getId(), now);
                        lastBooking = !pastBookings.isEmpty() ? pastBookings.getFirst().getEnd() : null;

                        List<Booking> futureBookings = bookingRepository.findFutureApprovedByItemId(item.getId(), now);
                        nextBooking = !futureBookings.isEmpty() ? futureBookings.getFirst().getStart() : null;
                    }

                    return new ItemBookingDto(
                            item.getId(),
                            item.getName(),
                            item.getDescription(),
                            item.getAvailable(),
                            item.getOwner().getId(),
                            item.getRequestId(),
                            lastBooking,
                            nextBooking,
                            comments.stream()
                                    .map(CommentMapper::toDto)
                                    .collect(Collectors.toList())
                    );
                })
                .collect(Collectors.toList());

        log.info("Найдено {} вещей для пользователя {}", result.size(), userId);
        return result;
    }

    private User getUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }
}