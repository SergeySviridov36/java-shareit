package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemDtoBooking;
import ru.practicum.shareit.user.User;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStart(booking.getStart());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setBooker(new BookingDto.Booker(booking.getBooker().getId(), booking.getBooker().getName()));
        bookingDto.setItem(new BookingDto.Item(booking.getItem().getId(), booking.getItem().getName()));
        return bookingDto;
    }

    public static Booking toBooking(BookingRequestDto bookingJson, Item item, User user) {
        Booking booking = new Booking();
        booking.setEnd(bookingJson.getEnd());
        booking.setStart(bookingJson.getStart());
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        return booking;
    }

    public static ItemDtoBooking.BookingItemDto toBookingItemDto(Booking booking) {
        ItemDtoBooking.BookingItemDto bookingDto = new ItemDtoBooking.BookingItemDto();
        bookingDto.setId(booking.getId());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setStart(booking.getStart());
        bookingDto.setBookerId(booking.getBooker().getId());
        return bookingDto;
    }
}
