package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {
    @Mock
    private final ItemRepository itemRepository;
    @Mock
    private final UserRepository userRepository;
    @Mock
    private final BookingRepository bookingRepository;

    private BookingService bookingService;
    private User user;

    @BeforeEach
    public void addEnvironsForAll() {
        bookingService = new BookingServiceImpl(itemRepository, userRepository, bookingRepository);
        user = new User();
        user.setId(1L);
        user.setName("UserTest");
        user.setEmail("UserTest@mail.ru");

        when(bookingRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
    }

    @Test
    void findAllBookingsByBookerTest() {
        when(bookingRepository.findAllByBooker_Id(any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> result = bookingService.findAllByBooker(1L, "ALL", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findAllByBooker_Id(any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void findPastBookingsByBookerTest() {
        when(bookingRepository.findByBooker_IdAndEndIsBefore(any(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> result = bookingService.findAllByBooker(1L, "PAST", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findByBooker_IdAndEndIsBefore(any(), any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void findFutureBookingsByBookerTest() {
        when(bookingRepository.findByBooker_IdAndStartIsAfter(any(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> result = bookingService.findAllByBooker(1L, "FUTURE", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findByBooker_IdAndStartIsAfter(any(), any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void findCurrentBookingsByBookerTest() {
        when(bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> result = bookingService.findAllByBooker(1L, "CURRENT", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findByBooker_IdAndStartIsBeforeAndEndIsAfter(any()
                , any(), any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void findWaitingBookingsByBookerTest() {
        when(bookingRepository.findByBooker_IdAndStartIsAfterAndStatusIs(any(), any(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> result = bookingService.findAllByBooker(1L, "WAITING", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findByBooker_IdAndStartIsAfterAndStatusIs(any()
                , any(), any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void findRejectedBookingsByBookerTest() {
        when(bookingRepository.findByBooker_IdAndStartIsAfterAndStatusIs(any(), any(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> result = bookingService.findAllByBooker(1L, "REJECTED", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findByBooker_IdAndStartIsAfterAndStatusIs(any()
                , any(), any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void findAllOwnerBookingsTest() {
        when(bookingRepository.findAllByItem_IdIn(any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> result = bookingService.findAllByOwner(1L, "ALL", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findAllByItem_IdIn(any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void findPastOwnerBookingsTest() {
        when(bookingRepository.findByItem_IdInAndEndIsBefore(any(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> result = bookingService.findAllByOwner(1L, "PAST", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findByItem_IdInAndEndIsBefore(any(), any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void findFutureOwnerBookingsTest() {
        when(bookingRepository.findByItem_IdInAndStartIsAfter(any(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> result = bookingService.findAllByOwner(1L, "FUTURE", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findByItem_IdInAndStartIsAfter(any(), any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void findCurrentOwnerBookingsTest() {
        when(bookingRepository.findByItem_IdInAndStartIsBeforeAndEndIsAfter(any(), any(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> result = bookingService.findAllByOwner(1L, "CURRENT", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findByItem_IdInAndStartIsBeforeAndEndIsAfter(any()
                , any(), any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void findWaitingOwnerBookingsTest() {
        when(bookingRepository.findByItem_IdInAndStartIsAfterAndStatusIs(any(), any(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> result = bookingService.findAllByOwner(1L, "WAITING", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findByItem_IdInAndStartIsAfterAndStatusIs(any()
                , any(), any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void findRejectedOwnerBookingsTest() {
        when(bookingRepository.findByItem_IdInAndStartIsAfterAndStatusIs(any(), any(), any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> result = bookingService.findAllByOwner(1L, "REJECTED", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(bookingRepository, times(1)).findByItem_IdInAndStartIsAfterAndStatusIs(any()
                , any(), any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void createBookingTest() {
        long userId = 1L;
        User user = new User();
        user.setId(2L);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        BookingRequestDto bookingRequestDto = new BookingRequestDto(1L, LocalDateTime.now()
                , LocalDateTime.now().plusDays(1));

        Item item = new Item();
        item.setId(3L);
        item.setOwner(user);
        item.setIsAvailable(true);

        when(itemRepository.findById(bookingRequestDto.getItemId()))
                .thenReturn(Optional.of(item));

        BookingDto result = bookingService.create(bookingRequestDto, userId);

        assertThat(result, notNullValue());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void findBookingTest() {
        long bookingId = 2L;

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        verify(userRepository, times(0)).findById(user.getId());
        verify(bookingRepository, times(0)).findById(bookingId);
        Assertions.assertThrows(NotFoundException.class, () -> bookingService.findById(user.getId(), bookingId));
    }

    @Test
    void setBookingStateApprovedTest() {
        long bookingId = 2L;
        boolean approved = true;
        Item item = new Item();
        item.setId(3L);
        item.setOwner(user);
        item.setIsAvailable(true);
        var booking = new Booking();
        booking.setId(3L);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setBooker(user);
        booking.setItem(item);

        when(bookingRepository.findByIdAndItemOwnerId(bookingId, user.getId()))
                .thenReturn(Optional.of(booking));

        BookingDto result = bookingService.update(bookingId, user.getId(), approved);

        assertThat(result, notNullValue());
        assertThat(result.getStatus(), equalTo(Status.APPROVED));
        verify(userRepository, times(1)).findById(user.getId());
        verify(bookingRepository, times(1)).findByIdAndItemOwnerId(bookingId, user.getId());
    }

    @Test
    void setBookingStateRejectedTest() {
        long bookingId = 2L;
        boolean approved = false;
        Item item = new Item();
        item.setId(3L);
        item.setOwner(user);
        item.setIsAvailable(true);
        Booking booking = new Booking();
        booking.setId(3L);
        booking.setStatus(Status.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setBooker(user);
        booking.setItem(item);

        when(bookingRepository.findByIdAndItemOwnerId(bookingId, user.getId()))
                .thenReturn(Optional.of(booking));

        BookingDto result = bookingService.update(bookingId, user.getId(), approved);

        assertThat(result, notNullValue());
        assertThat(result.getStatus(), equalTo(Status.REJECTED));
        verify(userRepository, times(1)).findById(user.getId());
        verify(bookingRepository, times(1)).findByIdAndItemOwnerId(bookingId, user.getId());
    }
}
