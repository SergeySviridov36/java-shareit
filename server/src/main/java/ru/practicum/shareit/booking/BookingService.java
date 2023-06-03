package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingRequestDto bookingJsonDto, Long userId);

    BookingDto update(Long bookingId, Long userId, boolean isApproved);

    BookingDto findById(Long userId, Long bookingId);

    List<BookingDto> findAllByBooker(Long userId, String state, PageRequest page);

    List<BookingDto> findAllByOwner(Long userId, String state, PageRequest page);
}
