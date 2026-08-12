package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Получен запрос на получение всех пользователей");
        List<UserDto> users = userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("Найдено {} пользователей", users.size());
        return users;
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Получен запрос на получение пользователя с id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        log.info("Получен запрос на создание пользователя: {}", userDto);

        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new BadRequestException("Имя не может быть пустым");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new BadRequestException("Email не может быть пустым");
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new ConflictException("Пользователь с email " + userDto.getEmail() + " уже существует");
        }

        User user = UserMapper.toUser(userDto);
        User saved = userRepository.save(user);
        log.info("Пользователь создан с id: {}", saved.getId());
        return UserMapper.toUserDto(saved);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Получен запрос на обновление пользователя с id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (!user.getEmail().equals(userDto.getEmail()) &&
                    userRepository.findByEmail(userDto.getEmail()).isPresent()) {
                throw new ConflictException("Email " + userDto.getEmail() + " уже занят");
            }
            user.setEmail(userDto.getEmail());
        }

        User updated = userRepository.save(user);
        log.info("Пользователь с id {} обновлён", id);
        return UserMapper.toUserDto(updated);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Получен запрос на удаление пользователя с id: {}", id);

        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        userRepository.deleteById(id);
        log.info("Пользователь с id {} удалён", id);
    }
}