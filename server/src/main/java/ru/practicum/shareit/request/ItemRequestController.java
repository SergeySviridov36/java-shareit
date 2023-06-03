package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundEntityExeption;

import java.util.List;

import static ru.practicum.shareit.util.Constants.*;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestServiceImpl itemRequestService;

    @PostMapping
    public ItemRequestDto createRequest(@RequestBody ItemRequestDto inputItemRequestDto,
                                        @RequestHeader(X_SHARER) Long userId) {
        ItemRequestDto createItemRequest = itemRequestService.create(inputItemRequestDto, userId);
        log.debug("Добавление запроса предмета с описанием пользователем: {}", userId);
        return createItemRequest;
    }

    @GetMapping
    public List<ItemRequestDto> findAllRequestByOwner(@RequestHeader(X_SHARER) Long userId) {
        List<ItemRequestDto> listReguest = itemRequestService.findAllRequestByOwner(userId);
        log.debug("Просмотр всех запросов пользователя с id: {}", userId);
        return listReguest;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllRequest(@RequestHeader(X_SHARER) Long userId,
                                               @RequestParam(value = FROM, defaultValue = "0") Integer from,
                                               @RequestParam(value = SIZE, defaultValue = "10") Integer size) {
        if (from < 0) {
            throw new NotFoundEntityExeption("Значение должно быть больше чем 0!");
        }
        final Sort sort = Sort.by("created").descending();
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, sort);
        final List<ItemRequestDto> itemRequestDto = itemRequestService.findAllRequest(userId, page);
        log.debug("Получен список всех запросов для пользователя: {}", userId);
        return itemRequestDto;

    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findByRequestId(@RequestHeader(X_SHARER) Long userId,
                                          @PathVariable Long requestId) {
        final ItemRequestDto itemRequestDto = itemRequestService.findByRequestId(userId, requestId);
        log.debug("Получен запрос с данными о предметах для бронирования: {}", requestId);
        return itemRequestDto;
    }
}
