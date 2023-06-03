package ru.practicum.shareit.request;

import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class ItemRequestMapper {
    public static ItemRequest dtoInRequest(ItemRequestDto itemRequestDto, User user) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDto.getDescription());
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        return itemRequest;
    }

    public static ItemRequestDto inRequestDto(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setCreated(itemRequest.getCreated());
        return itemRequestDto;
    }
}
