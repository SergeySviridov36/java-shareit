package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NotFoundEntityExeption;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.comment.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingMapper.toBookingItemDto;
import static ru.practicum.shareit.item.comment.CommentMapper.toComment;
import static ru.practicum.shareit.item.comment.CommentMapper.toCommentResponseDto;
import static ru.practicum.shareit.item.ItemMapper.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    @Override
    public ItemDto update(ItemDto inputItemDto, Long ownerId, Long itemId) {
        final Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id : " + itemId + " не найден."));
        if (!ownerId.equals(oldItem.getOwner().getId())) {
            throw new NotFoundException("Попытка обновления не принадлежащего владельцу предмета");
        }
        if (inputItemDto.getName() != null) {
            oldItem.setName(inputItemDto.getName());
        }
        if (inputItemDto.getDescription() != null) {
            oldItem.setDescription(inputItemDto.getDescription());
        }
        if (inputItemDto.getAvailable() != null) {
            oldItem.setIsAvailable(inputItemDto.getAvailable());
        }
        Item item = itemRepository.save(oldItem);
        return itemInDto(item);
    }

    @Transactional
    @Override
    public ItemDto create(ItemDto inputItemDto, Long ownerId) {
        final Item newItem = dtoInItem(inputItemDto);
        final User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id : " + ownerId + " не найден."));
        newItem.setOwner(owner);
        final Long requestId = inputItemDto.getRequestId();
        if (requestId != null) {
            final ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Запрос на бронирование вещи не найден."));
            newItem.setRequest(itemRequest);
        }
        return itemInDto(itemRepository.save(newItem));
    }

    @Override
    public ItemDtoBooking findItemById(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id : " + itemId + " не найден."));
        ;
        final List<CommentDtoResponse> comments = commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
        final ItemDtoBooking itemDtoBooking = toItemDtoBooking(item);
        itemDtoBooking.setComments(comments);
        final Long id = item.getOwner().getId();
        if (Objects.equals(userId, id)) {
            final List<Booking> bookingList = bookingRepository.findByItem_IdAndStatusIs(itemId, Status.APPROVED);
            return setDateBookings(itemDtoBooking, bookingList);
        }
        return itemDtoBooking;
    }

    @Override
    public List<ItemDtoBooking> findAllItemsOwner(Long id, PageRequest page) {
        final List<ItemDtoBooking> list = itemRepository.findAllByOwnerId(id)
                .stream()
                .map(ItemMapper::toItemDtoBooking)
                .collect(Collectors.toList());
        final List<Long> itemsId = list
                .stream()
                .map(ItemDtoBooking::getId)
                .collect(Collectors.toList());
        final List<Booking> bookingList = bookingRepository.findAllByItem_IdInAndStatusIs(itemsId, Status.APPROVED);
        return list
                .stream()
                .map(itemsDto -> setDateBookings(itemsDto, bookingList))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(Long userId, String text, PageRequest page) {
        return itemRepository.search(text)
                .stream()
                .map(ItemMapper::itemInDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDtoResponse createComment(Long userId, CommentDto commentDto, Long itemId) {
        final Comment comment = toComment(commentDto);
        final User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id : " + userId + " не найден."));
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id : " + itemId + " не найден."));
        List<Booking> bookings = bookingRepository.findByItem_IdAndEndIsBefore(itemId, comment.getCreated())
                .stream()
                .filter(booking -> Objects.equals(booking.getBooker().getId(), userId))
                .collect(Collectors.toList());
        if (bookings.isEmpty()) {
            throw new NotFoundEntityExeption("Ошибка, невозможно оставить отзыв.");
        }
        comment.setAuthor(author);
        comment.setItem(item);
        commentRepository.save(comment);
        return toCommentResponseDto(comment);
    }

    private ItemDtoBooking setDateBookings(ItemDtoBooking itemsDto, List<Booking> bookingList) {
        final LocalDateTime time = LocalDateTime.now();
        Optional<Booking> bookingLast = bookingList
                .stream()
                .filter(booking -> Objects.equals(booking.getItem().getId(), itemsDto.getId()))
                .sorted(Comparator.comparing(Booking::getEnd).reversed())
                .filter(booking -> booking.getStart().isBefore(time))
                .limit(1)
                .findAny();
        bookingLast.ifPresent(booking -> itemsDto.setLastBooking(toBookingItemDto(booking)));

        Optional<Booking> bookingNext = bookingList
                .stream()
                .filter(booking -> Objects.equals(booking.getItem().getId(), itemsDto.getId()))
                .sorted(Comparator.comparing(Booking::getStart))
                .filter(booking -> booking.getStart().isAfter(time))
                .limit(1)
                .findAny();
        bookingNext.ifPresent(booking -> itemsDto.setNextBooking(toBookingItemDto(booking)));
        return itemsDto;
    }
}
