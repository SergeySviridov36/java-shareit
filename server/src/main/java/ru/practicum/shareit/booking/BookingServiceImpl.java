package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundEntityExeption;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static ru.practicum.shareit.booking.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.BookingMapper.toBookingDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingDto create(BookingRequestDto bookingJsonDto, Long userId) {
        checkDateBooking(bookingJsonDto);
        final User user = findAndCheckUserId(userId);
        final Item item = findAndCheckAccessBookingItemId(bookingJsonDto, userId);
        final Booking booking = toBooking(bookingJsonDto, item, user);
        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto update(Long bookingId, Long userId, boolean isApproved) {
        User user = findAndCheckUserId(userId);
        Booking booking = bookingRepository.findByIdAndItemOwnerId(bookingId,userId)
                .orElseThrow(() -> new NotFoundException("Бронь с id : " + bookingId + " не найдена."));
        final Long id = booking.getItem().getOwner().getId();
        if (!Objects.equals(id, userId))
            throw new NotFoundException("Пользователь с id : " + userId + " не может обновить статус этого предмета.");
        if (!booking.getStatus().equals(Status.WAITING))
            throw new NotFoundEntityExeption("Статус бронирования должен быть WAITING.");
        if (isApproved)
            booking.setStatus(Status.APPROVED);
        else
            booking.setStatus(Status.REJECTED);
        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(Long userId, Long bookingId) {
        return toBookingDto(bookingRepository.findById(bookingId)
                .filter(b -> Objects.equals(b.getBooker().getId(), userId) || Objects.equals(b.getItem().getOwner().getId(), userId))
                .orElseThrow(() -> new NotFoundException("Бронирование с id : " + bookingId + " не найдено.")));
    }

    @Override
    public List<BookingDto> findAllByBooker(Long userId, String state, PageRequest pageRequest) {
        final BookingState bookingState = BookingState.valueOf(state);
        final User user = findAndCheckUserId(userId);
        final LocalDateTime date = LocalDateTime.now();
        final Sort sort = Sort.by("start").descending();
        Page<Booking> bookings;
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBooker_Id(userId, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId, date, date, pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndIsBefore(userId, date, pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartIsAfter(userId, date, pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStartIsAfterAndStatusIs(userId, date, pageRequest, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStartIsAfterAndStatusIs(userId, date, pageRequest, Status.REJECTED);
                break;
            default:
                return emptyList();
        }
        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllByOwner(Long userId, String state, PageRequest pageRequest) {
        final BookingState bookingState = BookingState.valueOf(state);
        final User user = findAndCheckUserId(userId);
        final List<Long> itemIdList = itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        final LocalDateTime date = LocalDateTime.now();
        final Sort sort = Sort.by("start").descending();
        Page<Booking> bookings;
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByItem_IdIn(itemIdList, pageRequest);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItem_IdInAndStartIsBeforeAndEndIsAfter(itemIdList, date, date, pageRequest);
                break;
            case PAST:
                bookings = bookingRepository.findByItem_IdInAndEndIsBefore(itemIdList, date, pageRequest);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItem_IdInAndStartIsAfter(itemIdList, date, pageRequest);
                break;
            case WAITING:
                bookings = bookingRepository.findByItem_IdInAndStartIsAfterAndStatusIs(itemIdList, date, pageRequest, Status.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItem_IdInAndStartIsAfterAndStatusIs(itemIdList, date, pageRequest, Status.REJECTED);
                break;
            default:
                return emptyList();
        }
        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    private void checkDateBooking(BookingRequestDto bookingJsonDto) {
        if (bookingJsonDto.getEnd().isBefore(bookingJsonDto.getStart()) ||
                bookingJsonDto.getEnd().equals(bookingJsonDto.getStart()))
            throw new NotFoundEntityExeption("Ошибка даты бронирования.");
    }

    private User findAndCheckUserId(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id : " + userId + " не найден."));
    }

    private Item findAndCheckAccessBookingItemId(BookingRequestDto bookingJsonDto, Long userId) {
        final Long itemId = bookingJsonDto.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id : " + itemId + " не найден."));
        if (!item.getIsAvailable())
            throw new NotFoundEntityExeption("Предмет не доступен для бронирования.");
        final Long id = item.getOwner().getId();
        if (Objects.equals(id, userId))
            throw new NotFoundException("Бронирование своего предмета запрещено.");
        return item;
    }
}

