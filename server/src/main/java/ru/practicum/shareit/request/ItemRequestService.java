package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto create(ItemRequestDto inputItemRequestDto, Long userId);

    List<ItemRequestDto> findAllRequestByOwner(Long userId);

    List<ItemRequestDto> findAllRequest(Long userId, PageRequest page);

    ItemRequestDto findByRequestId(Long userId, Long requestId);
}
