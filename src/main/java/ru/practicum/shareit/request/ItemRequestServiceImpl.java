package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.request.ItemRequestMapper.dtoInRequest;
import static ru.practicum.shareit.request.ItemRequestMapper.inRequestDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;


    @Override
    @Transactional
    public ItemRequestDto create(ItemRequestDto inputItemRequestDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id : " + userId + " не найден."));
        ItemRequest itemRequest = dtoInRequest(inputItemRequestDto, user);
        return inRequestDto(itemRequestRepository.save(itemRequest));

    }

    @Override
    public List<ItemRequestDto> findAllRequestByOwner(Long userId) {
        if (!checkUser(userId))
            throw new NotFoundException("Пользователь с id : " + userId + " не найден.");
        final Sort sort = Sort.by("created").descending();
        final List<ItemRequestDto> itemRequest = itemRequestRepository.findAllByRequestor_Id(userId, sort)
                .stream()
                .map(ItemRequestMapper::inRequestDto)
                .collect(Collectors.toList());
        return addItemsToRequest(itemRequest);
    }

    @Override
    public List<ItemRequestDto> findAllRequest(Long userId, PageRequest page) {
        if (!checkUser(userId))
            throw new NotFoundException("Пользователь с id : " + userId + " не найден.");
        final List<ItemRequestDto> itemRequest = itemRequestRepository.findAllByRequestor_IdNot(userId, page)
                .map(ItemRequestMapper::inRequestDto)
                .getContent();
        return addItemsToRequest(itemRequest);
    }

    @Override
    public ItemRequestDto findByRequestId(Long userId, Long requestId) {
        if (!checkUser(userId))
            throw new NotFoundException("Пользователь с id : " + userId + " не найден.");
        final ItemRequestDto itemRequest = inRequestDto(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос на бронирование вещи не найден.")));
        final List<ItemDto> itemList = itemRepository.findAllByRequestId(requestId)
                .stream()
                .map(ItemMapper::itemInDto)
                .collect(Collectors.toList());
        itemRequest.setItems(itemList);
        return itemRequest;
    }

    private Boolean checkUser(Long userId) {
        return userRepository.existsById(userId);
    }

    private List<ItemRequestDto> addItemsToRequest(List<ItemRequestDto> itemRequestDto) {
        final List<Long> listRequestIds = itemRequestDto
                .stream()
                .map(ItemRequestDto::getId)
                .collect(Collectors.toList());
        final List<ItemDto> itemDtoList = itemRepository.findAllByRequestIdIn(listRequestIds)
                .stream()
                .map(ItemMapper::itemInDto)
                .collect(Collectors.toList());
        for (ItemRequestDto re : itemRequestDto) {
            for (ItemDto itemDto : itemDtoList) {
                if (itemDto.getRequestId().equals(re.getId())) {
                    re.getItems().add(itemDto);
                }
            }
        }
        return itemRequestDto;
    }
}
