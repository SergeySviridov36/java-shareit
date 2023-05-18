package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestServiceImpl itemRequestService;

    private static final String USER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createRequest(@Valid @RequestBody ItemRequestDto inputItemRequestDto,
                                        @RequestHeader(USER) Long userId) {
        ItemRequestDto createItemRequest = itemRequestService.create(inputItemRequestDto, userId);
        log.debug("Добавление запроса предмета с описанием пользователем: {}", userId);
        return createItemRequest;
    }

    @GetMapping
    public List<ItemRequestDto> findAllRequestByOwner(@RequestHeader(USER) Long userId) {
        List<ItemRequestDto> listReguest = itemRequestService.findAllRequestByOwner(userId);
        log.debug("Просмотр всех запросов пользователя с id: {}", userId);
        return listReguest;
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllRequest(@RequestHeader(USER) Long userId,
                                               @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                               @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        final Sort sort = Sort.by("created").descending();
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, sort);
        final List<ItemRequestDto> itemRequestDto = itemRequestService.findAllRequest(userId, page);
        log.debug("Получен список всех запросов для пользователя: {}", userId);
        return itemRequestDto;

    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findByRequestId(@RequestHeader(USER) Long userId,
                                          @PathVariable Long requestId) {
        final ItemRequestDto itemRequestDto = itemRequestService.findByRequestId(userId, requestId);
        log.debug("Получен запрос с данными о предметах для бронирования: {}", requestId);
        return itemRequestDto;
    }
}
