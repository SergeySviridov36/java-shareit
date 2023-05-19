package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundEntityExeption;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentDtoResponse;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemService itemService;
    private static final String OWNER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto create(@RequestBody ItemDto inputItemDto,
                          @RequestHeader(OWNER) Long owner) {
        checkingCreating(inputItemDto);
        ItemDto createItem = itemService.create(inputItemDto, owner);
        log.debug("Добавление предмета пользователем: {}", owner);
        return createItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto inputItemDto,
                          @RequestHeader(OWNER) Long owner,
                          @PathVariable Long itemId) {
        ItemDto updateItem = itemService.update(inputItemDto, owner, itemId);
        log.debug("Обновление предмета с id: {}", itemId);
        return updateItem;
    }

    @GetMapping("/{itemId}")
    public ItemDtoBooking findItemById(@PathVariable Long itemId,
                                       @RequestHeader(OWNER) Long owner) {
        ItemDtoBooking itemDto = itemService.findItemById(itemId, owner);
        log.debug("Просмотр предмета с id: {}", itemId);
        return itemDto;
    }

    @GetMapping
    public List<ItemDtoBooking> findAllItems(@RequestHeader(OWNER) Long owner,
                                             @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                             @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<ItemDtoBooking> allItems = itemService.findAllItemsOwner(owner, page);
        log.debug("Получение списка всех предметов");
        return allItems;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestParam(value = "text") String text,
                                    @Valid @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                    @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        if (!text.isBlank()) {
            PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
            List<ItemDto> itemDtoList = itemService.searchItem(userId, text, page);
            log.debug("Поиск необходимого предмета");
            return itemDtoList;
        }
        return new ArrayList<>();
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @Valid @RequestBody CommentDto commentDto,
                                            @PathVariable Long itemId) {
        CommentDtoResponse newComment = itemService.createComment(userId, commentDto, itemId);
        log.debug("Добавлен отзыв для предмета с id : {}", itemId);
        return newComment;
    }

    private void checkingCreating(ItemDto inputItemDto) {
        if (inputItemDto.getName().isBlank() ||
                inputItemDto.getName() == null ||
                inputItemDto.getDescription() == null ||
                inputItemDto.getDescription().isBlank() ||
                inputItemDto.getAvailable() == null) {
            throw new NotFoundEntityExeption("Ошибка! Не все поля заполнены");
        }
    }
}
