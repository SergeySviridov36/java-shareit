package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker_IdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByBooker_Id(Long bookerId, Sort sort);

    List<Booking> findByBooker_IdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByBooker_IdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItem_IdIn(List<Long> itemId, Sort sort);

    List<Booking> findByItem_IdInAndStartIsBeforeAndEndIsAfter(List<Long> itemId, LocalDateTime date, LocalDateTime date1, Sort sort);

    List<Booking> findByItem_IdInAndEndIsBefore(List<Long> itemId, LocalDateTime date, Sort sort);

    List<Booking> findByItem_IdInAndStartIsAfterAndStatusIs(List<Long> itemId, LocalDateTime date, Sort sort, Status bookingStatus);

    List<Booking> findByBooker_IdAndStartIsAfterAndStatusIs(Long userId, LocalDateTime date, Sort sort, Status bookingStatus);

    List<Booking> findByItem_IdInAndStartIsAfter(List<Long> itemIdList, LocalDateTime date, Sort sort);

    List<Booking> findAllByItem_IdInAndStatusIs(List<Long> itemId, Status status);

    List<Booking> findByItem_IdAndStatusIs(Long itemId, Status status);

    List<Booking> findByItem_IdAndEndIsBefore(Long itemId, LocalDateTime date);
}
