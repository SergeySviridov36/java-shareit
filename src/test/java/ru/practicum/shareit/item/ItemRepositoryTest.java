package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private User owner;
    private ItemRequest itemRequest;
    final PageRequest page = PageRequest.of(0, 10);

    @BeforeEach
    public void addEnvironsForAll() {
        User user = new User();
        user.setName("userNameTest");
        user.setEmail("userTest.@mail.ru");
        owner = new User();
        owner.setName("ownerNameTest");
        owner.setEmail("OwnerTest@mail.ru");
        userRepository.save(user);
        userRepository.save(owner);
        itemRequest = new ItemRequest();
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("itemRequestDescriptionTest");
        itemRequestRepository.save(itemRequest);
        Item item = new Item();
        item.setName("itemNameTest");
        item.setDescription("itemDescriptionTest");
        item.setIsAvailable(Boolean.TRUE);
        item.setOwner(owner);
        item.setRequest(itemRequest);
        itemRepository.save(item);
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(3));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);
        Comment comment = new Comment();
        comment.setText("commentTextTest");
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        commentRepository.save(comment);
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(testEntityManager);
    }

    @Test
    void findAllByOwnerIdTest() {
        List<Item> result = itemRepository.findAllByOwnerId(owner.getId(), page)
                .stream()
                .collect(Collectors.toList());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void searchTest() {
        List<Item> result = itemRepository.search(
                        "itemNameTest", "itemNameTest", page)
                .stream()
                .collect(Collectors.toList());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void findAllByRequestIdInTest() {
        List<Item> result = itemRepository.findAllByRequestIdIn(List.of(itemRequest.getId()));

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void findAllByRequestIdTest() {
        List<Item> result = itemRepository.findAllByRequestId(itemRequest.getId());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void findAllByOwnerIdListTest() {
        List<Item> result = itemRepository.findAllByOwnerId(owner.getId());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }
}
