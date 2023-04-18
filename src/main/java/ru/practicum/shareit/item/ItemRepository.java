package ru.practicum.shareit.item;

import java.util.List;

public interface ItemRepository {
    Item create(Long ownerId, Item item);

    List<Item> findAllItemsOwner(Long ownerId);

    Item findItemById(Long itemId);

    Item update(Item dtoInItem);

    List<Item> searchItem(String text);
}
