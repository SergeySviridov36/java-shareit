package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private Booking booking;
    private Item item;
    private User user;
    final PageRequest page = PageRequest.of(0, 10);

    @BeforeEach
    public void addEnvironsForAll() {
        user = new User();
        user.setName("UserTest");
        user.setEmail("UserTest.@mail.ru");
        userRepository.save(user);
        item = new Item();
        item.setName("ItemTest");
        item.setDescription("descriptionTextTest");
        item.setIsAvailable(Boolean.TRUE);
        item.setOwner(user);
        itemRepository.save(item);
        booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(3));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(testEntityManager);
    }

    @Test
    void findAllByBooker_IdTest() {
        List<Booking> result = bookingRepository.findAllByBooker_Id(user.getId(), page)
                .stream()
                .collect(Collectors.toList());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void findByBooker_IdAndStartIsBeforeAndEndIsAfterTest() {
        List<Booking> result = bookingRepository.findByBooker_IdAndStartIsAfter(user.getId(), LocalDateTime.now(), page)
                .stream()
                .collect(Collectors.toList());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void findByBooker_IdAndEndIsBeforeTest() {
        List<Booking> result = bookingRepository.findByBooker_IdAndEndIsBefore(user.getId(), LocalDateTime.now(), page)
                .stream()
                .collect(Collectors.toList());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
    }

    @Test
    void findAllByItem_IdInTest() {
        List<Booking> result = bookingRepository.findAllByItem_IdIn(List.of(item.getId()), page)
                .stream()
                .collect(Collectors.toList());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void findByItem_IdInAndStartIsBeforeAndEndIsAfterTest() {
        List<Booking> result = bookingRepository.findByItem_IdInAndStartIsBeforeAndEndIsAfter(List.of(item.getId())
                        , LocalDateTime.now(), LocalDateTime.now(), page)
                .stream()
                .collect(Collectors.toList());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
    }

    @Test
    void findByItem_IdInAndEndIsBeforeTest() {
        List<Booking> result = bookingRepository.findByItem_IdInAndEndIsBefore(List.of(item.getId())
                        , LocalDateTime.now(), page)
                .stream()
                .collect(Collectors.toList());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
    }

    @Test
    void findByBooker_IdAndStartIsAfterAndStatusIsTest() {
        List<Booking> result = bookingRepository.findByBooker_IdAndStartIsAfterAndStatusIs(user.getId()
                        , LocalDateTime.now(), page, Status.WAITING)
                .stream()
                .collect(Collectors.toList());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void findByItem_IdInAndStartIsAfterAndStatusIsTest() {
        List<Booking> result = bookingRepository.findByItem_IdInAndStartIsAfterAndStatusIs(List.of(item.getId())
                        , LocalDateTime.now(), page, Status.WAITING)
                .stream()
                .collect(Collectors.toList());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void findByItem_IdInAndStartIsAfterTest() {
        List<Booking> result = bookingRepository.findAllByItem_IdInAndStatusIs(List.of(item.getId()), Status.WAITING);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void findByItem_IdAndStatusIsTest() {
        List<Booking> result = bookingRepository.findByItem_IdAndStatusIs(item.getId(), Status.WAITING);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void findByItem_IdAndEndIsBeforeTest() {
        List<Booking> result = bookingRepository.findByItem_IdAndEndIsBefore(item.getId(), LocalDateTime.now());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
    }

    @Test
    void findByIdAndItemOwnerIdTest() {
        Optional<Booking> result = bookingRepository.findByIdAndItemOwnerId(booking.getId(), user.getId());

        assertThat(result, notNullValue());
        assertThat(result.get().getStatus(), equalTo(Status.WAITING));
    }
}
