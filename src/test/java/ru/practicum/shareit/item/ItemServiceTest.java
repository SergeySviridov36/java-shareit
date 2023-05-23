package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentDtoResponse;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static ru.practicum.shareit.item.ItemMapper.dtoInItem;

@Transactional
@AutoConfigureTestDatabase
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private ItemService itemService;
    private User user;
    private User owner;
    private ItemRequest itemRequest;
    private ItemDto itemDto;
    private Booking booking;
    private Comment comment;

    @BeforeEach
    public void addEnvironsForAll() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
        user = new User();
        user.setName("userNameTest");
        user.setEmail("userTest@mail.ru");
        owner = new User();
        owner.setName("ownerNameTest");
        owner.setEmail("ownerTest@mail.ru");
        userRepository.save(user);
        userRepository.save(owner);
        itemRequest = new ItemRequest();
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("itemRequestDescriptionTest");
        itemRequestRepository.save(itemRequest);
        itemDto = new ItemDto();
        itemDto.setName("itemDtoNameTest");
        itemDto.setDescription("itemDtoDescriptionTest");
        itemDto.setAvailable(Boolean.TRUE);
        itemDto.setRequestId(itemRequest.getId());
        booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(Status.APPROVED);
        booking.setBooker(user);
        comment = new Comment();
        comment.setText("commentTextTest");
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
    }

    @Test
    void createItemTest() {
        ItemDto result = itemService.create(itemDto, owner.getId());

        assertThat(result, notNullValue());
        assertThat(result.getName(), equalTo(itemDto.getName()));
        assertThat(result.getRequestId(), equalTo(itemRequest.getId()));
    }

    @Test
    void updateItemTest() {
        long itemId = itemService.create(itemDto, owner.getId()).getId();
        itemDto.setName("itemDtoSetNameTest");
        itemDto.setDescription("itemDtoSetDescription");
        ItemDto result = itemService.update(itemDto, owner.getId(), itemId);

        assertThat(result, notNullValue());
        assertThat(result.getName(), equalTo(itemDto.getName()));
        assertThat(result.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void findItemByIdTest() {
        ItemDto itemResult = itemService.create(itemDto, owner.getId());
        Item item = dtoInItem(itemResult);
        item.setId(itemResult.getId());
        booking.setItem(item);
        comment.setItem(item);
        bookingRepository.save(booking);
        commentRepository.save(comment);

        ItemDtoBooking result = itemService.findItemById(item.getId(), owner.getId());

        assertThat(result, notNullValue());
        assertThat(result.getComments().size(), equalTo(1));
        assertThat(result.getNextBooking().getBookerId(), equalTo(booking.getBooker().getId()));
    }

    @Test
    void findAllItemsOwnerTest() {
        ItemDto itemResult = itemService.create(itemDto, owner.getId());
        Item item = dtoInItem(itemResult);
        item.setId(itemResult.getId());
        booking.setItem(item);
        bookingRepository.save(booking);

        List<ItemDtoBooking> result = itemService.findAllItemsOwner(owner.getId(), PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void searchItemsTest() {
        itemService.create(itemDto, owner.getId());
        String text = "itemDtoNameTest";
        List<ItemDto> result = itemService.searchItem(user.getId(), text, PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void createCommentTest() {
        ItemDto itemResult = itemService.create(itemDto, owner.getId());
        Item item = dtoInItem(itemResult);
        item.setId(itemResult.getId());
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(4));
        booking.setEnd(LocalDateTime.now().minusDays(2));
        bookingRepository.save(booking);
        CommentDto commendDto = new CommentDto();
        commendDto.setText("Отл");

        CommentDtoResponse result = itemService.createComment(user.getId(), commendDto, item.getId());

        assertThat(result, notNullValue());
        assertThat(result.getAuthorName(), equalTo(user.getName()));
    }
}
