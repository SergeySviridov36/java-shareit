package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constants.*;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookings(@RequestHeader(X_SHARER) long userId,
											  @RequestParam(name = STATE, defaultValue = "all") String stateParam,
											  @PositiveOrZero @RequestParam(name = FROM, defaultValue = "0") Integer from,
											  @Positive @RequestParam(name = SIZE, defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsOwner(@RequestHeader(X_SHARER) long userId,
												   @RequestParam(name = STATE, defaultValue = "all") String stateParam,
												   @PositiveOrZero @RequestParam(name = FROM, defaultValue = "0") Integer from,
												   @Positive @RequestParam(name = SIZE, defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookingsOwner(userId, state, from, size);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateBooking(@RequestHeader(X_SHARER) Long userId,
												@PathVariable Long bookingId,
												@RequestParam(value = "approved") String approved){
		boolean isApproved = approved.equals("true");
		log.info("Получен PATCH-запрос  бронирования с ID={}", bookingId);
		return bookingClient.update(userId, bookingId, isApproved);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(@RequestHeader(X_SHARER) long userId,
										   @RequestBody @Valid BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.bookItem(userId, requestDto);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader(X_SHARER) long userId,
											 @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}
}
