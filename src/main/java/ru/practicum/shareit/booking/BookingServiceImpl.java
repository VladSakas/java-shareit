package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
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
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto createBooking(Long userId, BookingRequestDto requestDto) {
        User booker = getUserEntity(userId);
        Item item = itemService.getItemById(requestDto.getItemId());  // ← теперь работает

        if (!item.getAvailable()) {
            throw new BadRequestException("Вещь недоступна для бронирования");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Владелец не может бронировать свою вещь");
        }

        if (requestDto.getStart() == null || requestDto.getEnd() == null) {
            throw new BadRequestException("Дата начала и окончания бронирования обязательны");
        }

        if (requestDto.getStart().isAfter(requestDto.getEnd()) || requestDto.getStart().equals(requestDto.getEnd())) {
            throw new BadRequestException("Дата начала должна быть раньше даты окончания");
        }

        if (requestDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата начала бронирования должна быть в будущем");
        }

        Booking booking = new Booking();
        booking.setStart(requestDto.getStart());
        booking.setEnd(requestDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        log.info("Создано бронирование: {}", booking);
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto updateBookingStatus(Long userId, Long bookingId, Boolean approved) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            throw new ForbiddenException("Пользователь не найден или доступ запрещен");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Только владелец вещи может подтвердить или отклонить бронирование");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BadRequestException("Статус бронирования должен быть WAITING");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        log.info("Статус бронирования {} обновлён на {}", bookingId, booking.getStatus());
        Booking updatedBooking = bookingRepository.save(booking);
        return bookingMapper.toDto(updatedBooking);
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        userService.getUserById(userId);

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id " + bookingId + " не найдено"));

        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Пользователь не имеет доступа к этому бронированию");
        }

        return bookingMapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByUser(Long userId, String state) {
        userService.getUserById(userId);
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state != null ? state.toUpperCase() : "ALL") {
            case "ALL" -> bookingRepository.findByBookerIdOrderByStartDesc(userId);
            case "CURRENT" -> bookingRepository.findCurrentByBooker(userId, now);
            case "PAST" -> bookingRepository.findPastByBooker(userId, now);
            case "FUTURE" -> bookingRepository.findFutureByBooker(userId, now);
            case "WAITING" -> bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            case "REJECTED" ->
                    bookingRepository.findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            default -> throw new BadRequestException("Неизвестный статус: " + state);
        };

        log.info("Получено {} бронирований для пользователя {} с фильтром {}", bookings.size(), userId, state);
        return bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingsByOwner(Long userId, String state) {
        userService.getUserById(userId);

        LocalDateTime now = LocalDateTime.now();
        String stateUpper = state != null ? state.toUpperCase() : "ALL";
        List<Booking> bookings = switch (stateUpper) {
            case "ALL" -> bookingRepository.findByOwnerId(userId);
            case "CURRENT" -> bookingRepository.findCurrentByOwner(userId, now);
            case "PAST" -> bookingRepository.findPastByOwner(userId, now);
            case "FUTURE" -> bookingRepository.findFutureByOwner(userId, now);
            case "WAITING" -> bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.WAITING);
            case "REJECTED" -> bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.REJECTED);
            default -> throw new BadRequestException("Неизвестный статус: " + state);
        };

        log.info("Получено {} бронирований для владельца {} с фильтром {}", bookings.size(), userId, state);
        return bookings.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    private User getUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
    }
}