package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;
    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@RequestHeader(HEADER) Long userId,
                             @Valid @RequestBody BookingDtoJson bookingJsonDto) {
        BookingDto bookingDto = bookingService.create(bookingJsonDto, userId);
        log.debug("Забронирован предмет с id : {}", bookingJsonDto.getItemId());
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader(HEADER) Long userId,
                             @PathVariable Long bookingId,
                             @RequestParam(value = "approved") String approved) {
        boolean isApproved = approved.equals("true");
        BookingDto bookingDto = bookingService.update(bookingId, userId, isApproved);
        log.debug("Обновлен статус бронирования предмета с id : {}", bookingId);
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader(HEADER) Long userId,
                               @PathVariable Long bookingId) {
        BookingDto bookingDto = bookingService.findById(userId, bookingId);
        log.debug("Информация о бронировании с id : {}", bookingId);
        return bookingDto;
    }

    @GetMapping
    public List<BookingDto> findAllByBooker(@RequestHeader(HEADER) Long userId,
                                            @RequestParam(value = "state", defaultValue = "ALL", required = false) String state) {
        List<BookingDto> bookingDtoList = bookingService.findAllByBooker(userId, state);
        log.debug("Получен список забронированных предметов пользователя с id : {}", userId);
        return bookingDtoList;
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwner(@RequestHeader(HEADER) Long userId,
                                           @RequestParam(value = "state", defaultValue = "ALL", required = false) String state) {
        List<BookingDto> bookingDtoList = bookingService.findAllByOwner(userId, state);
        log.debug("Получен список забронированных предметов пользователя с id : {}", userId);
        return bookingDtoList;
    }
}
