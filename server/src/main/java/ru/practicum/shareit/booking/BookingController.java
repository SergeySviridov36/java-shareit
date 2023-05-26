package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotFoundEntityExeption;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.util.Constants.*;

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
@Validated
public class BookingController {
    private final BookingService bookingService;
    private final Sort sort = Sort.by("start").descending();

    @PostMapping
    public BookingDto create(@RequestHeader(X_SHARER) Long userId,
                             @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        BookingDto bookingDto = bookingService.create(bookingRequestDto, userId);
        log.debug("Забронирован предмет с id : {}", bookingRequestDto.getItemId());
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader(X_SHARER) Long userId,
                             @PathVariable Long bookingId,
                             @RequestParam(value = "approved") String approved) {
        boolean isApproved = approved.equals("true");
        BookingDto bookingDto = bookingService.update(bookingId, userId, isApproved);
        log.debug("Обновлен статус бронирования предмета с id : {}", bookingId);
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader(X_SHARER) Long userId,
                               @PathVariable Long bookingId) {
        BookingDto bookingDto = bookingService.findById(userId, bookingId);
        log.debug("Информация о бронировании с id : {}", bookingId);
        return bookingDto;
    }

    @GetMapping
    public List<BookingDto> findAllByBooker(@RequestHeader(X_SHARER) Long userId,
                                            @RequestParam(value = STATE, defaultValue = "ALL", required = false) String state,
                                            @RequestParam(value = FROM, defaultValue = "0") Integer from,
                                            @RequestParam(value = SIZE, defaultValue = "10") Integer size) {
        if (from < 0) {
            throw new NotFoundEntityExeption("Значение должно быть больше чем 0!");
        }
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, sort);
        List<BookingDto> bookingDtoList = bookingService.findAllByBooker(userId, state, page);
        log.debug("Получен список забронированных предметов пользователя с id : {}", userId);
        return bookingDtoList;
    }

    @GetMapping("/owner")
    public List<BookingDto> findAllByOwner(@RequestHeader(X_SHARER) Long userId,
                                           @RequestParam(value = STATE, defaultValue = "ALL", required = false) String state,
                                           @RequestParam(value = FROM, defaultValue = "0") Integer from,
                                           @RequestParam(value = SIZE, defaultValue = "10") Integer size) {
        if (from < 0) {
            throw new NotFoundEntityExeption("Значение должно быть больше чем 0!");
        }
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, sort);
        List<BookingDto> bookingDtoList = bookingService.findAllByOwner(userId, state, page);
        log.debug("Получен список забронированных предметов пользователя с id : {}", userId);
        return bookingDtoList;
    }
}
