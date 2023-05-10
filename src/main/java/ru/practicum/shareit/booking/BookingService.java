package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingDtoJson bookingJsonDto, Long userId);

    BookingDto update(Long bookingId, Long userId, boolean isApproved);

    BookingDto findById(Long userId, Long bookingId);

    List<BookingDto> findAllByBooker(Long userId, String state);

    List<BookingDto> findAllByOwner(Long userId, String state);
}
