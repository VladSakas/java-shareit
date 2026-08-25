package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper mapper;

    @Override
    @Transactional
    public ItemRequestDto createRequest(Long userId, ItemRequestDto requestDto) {
        userService.getUserById(userId);

        User requestor = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        if (requestDto.getDescription() == null || requestDto.getDescription().isBlank()) {
            throw new BadRequestException("Описание запроса не может быть пустым");
        }

        ItemRequest request = mapper.toEntity(requestDto, requestor);
        ItemRequest saved = requestRepository.save(request);
        log.info("Создан запрос с id: {} пользователем {}", saved.getId(), userId);
        return mapper.toDto(saved, List.of());
    }

    @Override
    public List<ItemRequestDto> getRequestsByUser(Long userId) {
        userService.getUserById(userId);
        List<ItemRequest> requests = requestRepository.findByRequestorIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(req -> mapper.toDto(req, getItemsForRequest(req.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId) {
        userService.getUserById(userId);
        List<ItemRequest> requests = requestRepository.findByRequestorIdNotOrderByCreatedDesc(userId);
        return requests.stream()
                .map(req -> mapper.toDto(req, getItemsForRequest(req.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userService.getUserById(userId);
        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));
        return mapper.toDto(request, getItemsForRequest(requestId));
    }

    private List<ItemResponseDto> getItemsForRequest(Long requestId) {
        return itemRepository.findByRequestId(requestId).stream()
                .map(item -> new ItemResponseDto(
                        item.getId(),
                        item.getName(),
                        item.getOwner().getId()
                ))
                .collect(Collectors.toList());
    }
}