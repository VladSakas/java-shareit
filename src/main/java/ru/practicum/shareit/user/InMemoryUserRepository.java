package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.*;

@Slf4j
@Repository
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private long nextId = 1;

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(nextId++);
            users.put(user.getId(), user);
            log.info("Создан пользователь с ID: {}", user.getId());
        } else {
            users.put(user.getId(), user);
            log.info("Обновлен пользователь с ID: {}", user.getId());
        }
        return user;
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
        log.info("Удален пользователь с ID: {}", id);
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null) {
            return false;
        }
        return users.values().stream()
                .filter(user -> user.getEmail() != null)
                .anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public boolean existsById(Long id) {
        return users.containsKey(id);
    }
}