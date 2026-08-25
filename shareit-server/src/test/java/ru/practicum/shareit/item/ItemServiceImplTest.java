package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ShareItServer.class)
@Transactional
class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void addNewItem_shouldCreateItem() {
        User owner = new User();
        owner.setName("Test User");
        owner.setEmail("test@user.com");
        User savedUser = userRepository.save(owner);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        ItemDto created = itemService.addNewItem(savedUser.getId(), itemDto);

        assertNotNull(created);
        assertNotNull(created.getId());
        assertEquals("Test Item", created.getName());
        assertEquals("Test Description", created.getDescription());
        assertTrue(created.getAvailable());
        assertEquals(savedUser.getId(), created.getOwnerId());
    }

    @Test
    void updateItem_shouldUpdateItem() {
        User owner = new User();
        owner.setName("Test User");
        owner.setEmail("test@user.com");
        User savedUser = userRepository.save(owner);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Old Name");
        itemDto.setDescription("Old Description");
        itemDto.setAvailable(true);
        ItemDto created = itemService.addNewItem(savedUser.getId(), itemDto);

        ItemDto updateDto = new ItemDto();
        updateDto.setName("New Name");
        updateDto.setDescription("New Description");
        updateDto.setAvailable(false);

        ItemDto updated = itemService.updateItem(savedUser.getId(), created.getId(), updateDto);

        assertEquals("New Name", updated.getName());
        assertEquals("New Description", updated.getDescription());
        assertFalse(updated.getAvailable());
    }

    @Test
    void getItemById_shouldReturnItem() {
        User owner = new User();
        owner.setName("Test User");
        owner.setEmail("test@user.com");
        User savedUser = userRepository.save(owner);

        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        ItemDto created = itemService.addNewItem(savedUser.getId(), itemDto);

        Item found = itemService.getItemById(created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("Test Item", found.getName());
    }
}