package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();           // ← было List<User>

    UserDto getUserById(Long id);          // ← было User

    UserDto createUser(UserDto userDto);   // ← было User saveUser(User user)

    UserDto updateUser(Long id, UserDto userDto); // ← было User updateUser(Long id, User user)

    void deleteUser(Long id);              // ← без изменений
}