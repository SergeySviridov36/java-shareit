package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exception.NotFoundEntityExeption;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {
    @Mock
    private final ItemRepository itemRepository;
    @Mock
    private final UserRepository userRepository;
    @Mock
    private final BookingRepository bookingRepository;
    @Mock
    private final CommentRepository commentRepository;
    @Mock
    private final ItemRequestRepository itemRequestRepository;
    private ItemService itemService;
    private User user;

    @BeforeEach
    public void addEnvironsForAll() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
        user = new User();
        user.setId(1L);
        user.setName("userNameTest");
        user.setEmail("userEmailTest@mail.ru");

        when(itemRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
    }

    @Test
    void createItemTest() {
        User user = new User();
        user.setId(2L);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setRequestor(user);
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));

        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(itemRequest.getId());

        ItemDto result = itemService.create(itemDto, 1L);

        assertThat(result, notNullValue());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateItemTest() {
        Item item = new Item();
        item.setId(1L);
        item.setOwner(user);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        ItemDto itemDto = new ItemDto();
        ItemDto result = itemService.update(itemDto, user.getId(), item.getId());

        assertThat(result, notNullValue());
        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void findItemByIdTest() {
        Item item = new Item();
        item.setId(1L);
        item.setOwner(user);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        User booker = new User();
        booker.setId(88L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setStatus(Status.APPROVED);
        booking.setBooker(booker);
        when(bookingRepository.findByItem_IdAndStatusIs(anyLong(), any()))
                .thenReturn(List.of(booking));

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setItem(item);
        comment.setAuthor(booker);
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(List.of(comment));

        ItemDtoBooking result = itemService.findItemById(item.getId(), user.getId());

        assertThat(result, notNullValue());
        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findAllByItemId(anyLong());
        verify(bookingRepository, times(1)).findByItem_IdAndStatusIs(anyLong(), any());
    }

    @Test
    void findAllItemsOwnerTest() {
        Item item = new Item();
        item.setId(1L);
        item.setOwner(user);
        when(itemRepository.findAllByOwnerId(anyLong(), any()))
                .thenReturn(Page.empty());
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        User booker = new User();
        booker.setId(56L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setStatus(Status.APPROVED);
        booking.setBooker(booker);
        when(bookingRepository.findAllByItem_IdInAndStatusIs(anyList(), any()))
                .thenReturn(Collections.emptyList());

        List<ItemDtoBooking> result = itemService.findAllItemsOwner(user.getId(), PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(1)).findAllByOwnerId(anyLong(), any());
        verify(bookingRepository, times(1)).findAllByItem_IdInAndStatusIs(anyList(), any());
    }

    @Test
    void searchItemsTest() {
        when(itemRepository.search(
                anyString(), anyString(), any()))
                .thenReturn(Page.empty());
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        List<ItemDto> result = itemService.searchItem(user.getId(), "itemNameTest", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRepository, times(1))
                .search(anyString(), anyString(), any());
    }

    @Test
    void createCommentTest() {
        var commentDto = new CommentDto();
        commentDto.setText("commentDtoText");

        Item item = new Item();
        item.setId(1L);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        when(bookingRepository.findByItem_IdAndEndIsBefore(anyLong(), any()))
                .thenReturn(Collections.emptyList());

        verify(userRepository, times(0)).existsById(user.getId());
        verify(itemRepository, times(0)).findById(anyLong());
        verify(bookingRepository, times(0)).findByItem_IdAndEndIsBefore(anyLong(), any());
        Assertions.assertThrows(NotFoundEntityExeption.class, () -> itemService.createComment(user.getId(), commentDto, 1L));
    }
}
