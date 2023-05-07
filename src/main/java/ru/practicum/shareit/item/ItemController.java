package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundEntityExeption;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final UserService userService;
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
    public List<ItemDtoBooking> findAllItems(@RequestHeader(OWNER) Long owner) {
        List<ItemDtoBooking> allItems = itemService.findAllItemsOwner(owner);
        log.debug("Получение списка всех предметов");
        return allItems;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam(value = "text") String text) {
        if (!text.isBlank()) {
            List<ItemDto> itemDtoList = itemService.searchItem(text);
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
