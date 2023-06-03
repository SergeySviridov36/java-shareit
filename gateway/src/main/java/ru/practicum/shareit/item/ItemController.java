package ru.practicum.shareit.item;

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
import static ru.practicum.shareit.util.Constants.FROM;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto,
                                         @RequestHeader(X_SHARER) long userId) {
        log.info("Создание предмета {}", itemDto);
        return itemClient.create(userId,itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestBody ItemDto inputItemDto,
                                         @RequestHeader(X_SHARER) long userId,
                                         @PathVariable Long itemId) {
        log.debug("Обновление предмета с id: {}", itemId);
        return itemClient.update(inputItemDto, userId, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@PathVariable Long itemId,
                                               @RequestHeader(X_SHARER) Long owner) {
        log.debug("Просмотр предмета с id: {}", itemId);
        return itemClient.findItemById(itemId, owner);
    }

    @GetMapping
    public ResponseEntity<Object> findAllItems(@RequestHeader(X_SHARER) Long owner,
                                               @PositiveOrZero @RequestParam(value = FROM, defaultValue = "0") Integer from,
                                               @Positive @RequestParam(value = SIZE, defaultValue = "10") Integer size) {
        log.debug("Получение списка всех предметов");
        return itemClient.findAllItems(owner, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(X_SHARER) Long userId,
                                             @Valid @RequestParam(value = "text") String text,
                                             @Valid @PositiveOrZero @RequestParam(value = FROM, defaultValue = "0") Integer from,
                                             @RequestParam(value = "size", defaultValue = "10") Integer size) {
            log.debug("Поиск необходимого предмета");
            return itemClient.searchItem(userId, text, from, size);
        }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(X_SHARER) Long userId,
                                                @Valid @RequestBody CommentDto commentDto,
                                                @PathVariable Long itemId) {
        log.debug("Добавлен отзыв для предмета с id : {}", itemId);
        return itemClient.createComment(userId, commentDto, itemId);
    }
}
