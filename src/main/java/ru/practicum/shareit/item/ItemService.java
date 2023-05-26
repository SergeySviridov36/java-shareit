package ru.practicum.shareit.item;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentDtoResponse;

import java.util.List;

public interface ItemService {
    ItemDto update(ItemDto inputItemDto, Long ownerId, Long itemId);

    ItemDto create(ItemDto inputItemDto, Long ownerId);

    ItemDtoBooking findItemById(Long itemId, Long userId);

    List<ItemDtoBooking> findAllItemsOwner(Long ownerId, PageRequest page);

    List<ItemDto> searchItem(Long userId, String text, PageRequest page);

    CommentDtoResponse createComment(Long userId, CommentDto commentDto, Long itemId);
}



