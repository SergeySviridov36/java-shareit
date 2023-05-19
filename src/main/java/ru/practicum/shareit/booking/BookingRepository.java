package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, PageRequest pageRequest);

    Page<Booking> findAllByBooker_Id(Long bookerId, PageRequest pageRequest);

    Page<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end, PageRequest pageRequest);

    Page<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime start, PageRequest pageRequest);

    Page<Booking> findAllByItem_IdIn(List<Long> itemId, PageRequest pageRequest);

    Page<Booking> findByItem_IdInAndStartIsBeforeAndEndIsAfter(List<Long> itemId, LocalDateTime date, LocalDateTime date1, PageRequest pageRequest);

    Page<Booking> findByItem_IdInAndEndIsBefore(List<Long> itemId, LocalDateTime date, PageRequest pageRequest);

    Page<Booking> findByItem_IdInAndStartIsAfterAndStatusIs(List<Long> itemId, LocalDateTime date, PageRequest pageRequest, Status bookingStatus);

    Page<Booking> findByBooker_IdAndStartIsAfterAndStatusIs(Long userId, LocalDateTime date, PageRequest pageRequest, Status bookingStatus);

    Page<Booking> findByItem_IdInAndStartIsAfter(List<Long> itemIdList, LocalDateTime date, PageRequest pageRequest);

    List<Booking> findAllByItem_IdInAndStatusIs(List<Long> itemId, Status status);

    List<Booking> findByItem_IdAndStatusIs(Long itemId, Status status);

    List<Booking> findByItem_IdAndEndIsBefore(Long itemId, LocalDateTime date);
}
