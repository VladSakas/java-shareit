package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItServer.class)
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Test
    void createUser_shouldCreateUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@user.com");

        UserDto created = userService.createUser(userDto);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Test User", created.getName());
        assertEquals("test@user.com", created.getEmail());
    }

    @Test
    void getUserById_shouldReturnUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@user.com");
        UserDto created = userService.createUser(userDto);

        UserDto found = userService.getUserById(created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("Test User", found.getName());
    }

    @Test
    void updateUser_shouldUpdateUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Old Name");
        userDto.setEmail("old@user.com");
        UserDto created = userService.createUser(userDto);

        UserDto updateDto = new UserDto();
        updateDto.setName("New Name");
        updateDto.setEmail("new@user.com");

        UserDto updated = userService.updateUser(created.getId(), updateDto);

        assertEquals("New Name", updated.getName());
        assertEquals("new@user.com", updated.getEmail());
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        UserDto userDto = new UserDto();
        userDto.setName("Test User");
        userDto.setEmail("test@user.com");
        UserDto created = userService.createUser(userDto);

        userService.deleteUser(created.getId());

        assertThrows(NotFoundException.class, () -> {
            userService.getUserById(created.getId());
        });
    }
}