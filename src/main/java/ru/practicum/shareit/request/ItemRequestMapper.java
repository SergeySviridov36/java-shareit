package ru.practicum.shareit.request;

public class ItemRequestMapper {
    public static ItemRequestDto inItemReqestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(
                itemRequest.getRequestor(),
                itemRequest.getDescription(),
                itemRequest.getCreated()
        );
    }
}
