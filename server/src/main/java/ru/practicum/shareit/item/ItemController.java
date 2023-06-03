package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentDtoResponse;

import java.util.List;

import static ru.practicum.shareit.util.Constants.*;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;


    @PostMapping
    public ItemDto create(@RequestBody ItemDto inputItemDto,
                          @RequestHeader(X_SHARER) Long owner) {
        ItemDto createItem = itemService.create(inputItemDto, owner);
        log.debug("Добавление предмета пользователем: {}", owner);
        return createItem;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto inputItemDto,
                          @RequestHeader(X_SHARER) Long owner,
                          @PathVariable Long itemId) {
        ItemDto updateItem = itemService.update(inputItemDto, owner, itemId);
        log.debug("Обновление предмета с id: {}", itemId);
        return updateItem;
    }

    @GetMapping("/{itemId}")
    public ItemDtoBooking findItemById(@PathVariable Long itemId,
                                       @RequestHeader(X_SHARER) Long owner) {
        ItemDtoBooking itemDto = itemService.findItemById(itemId, owner);
        log.debug("Просмотр предмета с id: {}", itemId);
        return itemDto;
    }

    @GetMapping
    public List<ItemDtoBooking> findAllItems(@RequestHeader(X_SHARER) Long owner,
                                             @RequestParam(value = FROM, defaultValue = "0") Integer from,
                                             @RequestParam(value = SIZE, defaultValue = "10") Integer size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, sort);
        List<ItemDtoBooking> allItems = itemService.findAllItemsOwner(owner, page);
        log.debug("Получение списка всех предметов");
        return allItems;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestHeader(X_SHARER) Long userId,
                                    @RequestParam(value = "text") String text,
                                    @RequestParam(value = FROM, defaultValue = "0") Integer from,
                                    @RequestParam(value = SIZE, defaultValue = "10") Integer size) {

            PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
            List<ItemDto> itemDtoList = itemService.searchItem(userId, text, page);
            log.debug("Поиск необходимого предмета");
            return itemDtoList;
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse createComment(@RequestHeader(X_SHARER) Long userId,
                                            @RequestBody CommentDto commentDto,
                                            @PathVariable Long itemId) {
        CommentDtoResponse newComment = itemService.createComment(userId, commentDto, itemId);
        log.debug("Добавлен отзыв для предмета с id : {}", itemId);
        return newComment;
    }
}
