package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = ShareItServer.class)
@Transactional
class BookingServiceImplTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    private LocalDateTime now = LocalDateTime.now();

    private UserDto createUser(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setName(name);
        userDto.setEmail(email);
        return userService.createUser(userDto);
    }

    private ItemDto createItem(Long userId, String name, String description, Boolean available) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName(name);
        itemDto.setDescription(description);
        itemDto.setAvailable(available);
        return itemService.addNewItem(userId, itemDto);
    }

    @Test
    void createBooking_shouldCreateBooking() {
        UserDto owner = createUser("Owner", "owner@test.com");
        ItemDto item = createItem(owner.getId(), "Test Item", "Test Desc", true);
        UserDto booker = createUser("Booker", "booker@test.com");

        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(item.getId());
        requestDto.setStart(now.plusDays(1));
        requestDto.setEnd(now.plusDays(2));

        BookingDto created = bookingService.createBooking(booker.getId(), requestDto);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals(BookingStatus.WAITING, created.getStatus());
        assertEquals(item.getId(), created.getItem().getId());
        assertEquals(booker.getId(), created.getBooker().getId());
    }

    @Test
    void updateBookingStatus_shouldApproveBooking() {
        UserDto owner = createUser("Owner", "owner@test.com");
        ItemDto item = createItem(owner.getId(), "Test Item", "Test Desc", true);
        UserDto booker = createUser("Booker", "booker@test.com");

        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(item.getId());
        requestDto.setStart(now.plusDays(1));
        requestDto.setEnd(now.plusDays(2));

        BookingDto created = bookingService.createBooking(booker.getId(), requestDto);

        BookingDto approved = bookingService.updateBookingStatus(
                owner.getId(),
                created.getId(),
                true
        );

        assertEquals(BookingStatus.APPROVED, approved.getStatus());
    }

    @Test
    void getBookingById_shouldReturnBooking() {
        UserDto owner = createUser("Owner", "owner@test.com");
        ItemDto item = createItem(owner.getId(), "Test Item", "Test Desc", true);
        UserDto booker = createUser("Booker", "booker@test.com");

        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(item.getId());
        requestDto.setStart(now.plusDays(1));
        requestDto.setEnd(now.plusDays(2));

        BookingDto created = bookingService.createBooking(booker.getId(), requestDto);

        BookingDto found = bookingService.getBookingById(booker.getId(), created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals(created.getStatus(), found.getStatus());
    }
}