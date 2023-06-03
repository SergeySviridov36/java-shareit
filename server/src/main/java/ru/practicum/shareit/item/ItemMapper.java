package ru.practicum.shareit.item;

public class ItemMapper {
    public static Item dtoInItem(ItemDto itemDto) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setIsAvailable(itemDto.getAvailable());
        item.setOwner(itemDto.getOwner());
        return item;
    }

    public static ItemDto itemInDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getOwner(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static ItemDtoBooking toItemDtoBooking(Item item) {
        return new ItemDtoBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getOwner(),
                item.getRequest() != null ? item.getRequest().getId() : null);
    }
}
