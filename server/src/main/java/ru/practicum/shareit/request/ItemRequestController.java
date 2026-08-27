package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestBody ItemRequestDto requestDto) {
        log.info("POST /requests - user {}", userId);
        return requestService.createRequest(userId, requestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getRequestsByUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests - user {}", userId);
        return requestService.getRequestsByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests/all - user {}", userId);
        return requestService.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long requestId) {
        log.info("GET /requests/{} - user {}", requestId, userId);
        return requestService.getRequestById(userId, requestId);
    }
}