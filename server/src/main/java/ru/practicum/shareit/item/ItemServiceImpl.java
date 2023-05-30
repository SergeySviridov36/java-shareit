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
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingMapper.toBookingItemDto;
import static ru.practicum.shareit.item.comment.CommentMapper.toComment;
import static ru.practicum.shareit.item.comment.CommentMapper.toCommentDtoResponse;
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
        if (!userRepository.existsById(ownerId)) {
            throw new NotFoundException("Пользователь с id : " + ownerId + " не найден.");
        }
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
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id : " + userId + " не найден.");
        }
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id : " + itemId + " не найден."));
        ;
        final List<CommentDtoResponse> comments = commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentDtoResponse)
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
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с id : " + id + " не найден.");
        }
        final List<ItemDtoBooking> list = itemRepository.findAllByOwnerId(id, page)
                .stream()
                .map(ItemMapper::toItemDtoBooking)
                .collect(Collectors.toList());
        final List<Long> itemsId = list
                .stream()
                .map(ItemDtoBooking::getId)
                .collect(Collectors.toList());
        final List<Comment> comments = commentRepository.findAll();
        final List<Booking> bookingList = bookingRepository.findAllByItem_IdInAndStatusIs(itemsId, Status.APPROVED);
        return list
                .stream()
                .map(itemsDto -> setDateBookings(itemsDto, bookingList))
                .map(itemDto -> addCommentsInItem(itemDto, comments))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchItem(Long userId, String text, PageRequest page) {
        if (text == null || text.isBlank())
            return Collections.emptyList();
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id : " + userId + " не найден.");
        }
        return itemRepository.search(text, text, page)
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
        return toCommentDtoResponse(comment);
    }

    private ItemDtoBooking addCommentsInItem(ItemDtoBooking item, List<Comment> comments) {
        List<CommentDtoResponse> commentDtoList = comments
                .stream()
                .filter(c -> c.getItem().getId().equals(item.getId()))
                .map(CommentMapper::toCommentDtoResponse)
                .collect(Collectors.toList());
        item.setComments(commentDtoList);
        return item;
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