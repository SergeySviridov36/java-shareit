package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constants.*;
import static ru.practicum.shareit.util.Constants.X_SHARER;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemRequestDto inputItemRequestDto,
                                         @RequestHeader(X_SHARER) Long userId) {
        log.debug("Добавление запроса предмета с описанием пользователем: {}", userId);
        return itemRequestClient.create(userId, inputItemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findAllRequestByOwner(@RequestHeader(X_SHARER) Long userId) {
        log.debug("Просмотр всех запросов пользователя с id: {}", userId);
        return itemRequestClient.findAllRequestByOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllRequest(@RequestHeader(X_SHARER) Long userId,
                                                 @PositiveOrZero @RequestParam(value = FROM, defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(value = SIZE, defaultValue = "10") Integer size) {
        log.debug("Получен список всех запросов для пользователя: {}", userId);
        return itemRequestClient.findAllRequest(userId, from, size);

    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findByRequestId(@RequestHeader(X_SHARER) Long userId,
                                                  @PathVariable Long requestId) {
        log.debug("Получен запрос с данными о предметах для бронирования: {}", requestId);
        return itemRequestClient.findByRequestId(userId, requestId);
    }
}
