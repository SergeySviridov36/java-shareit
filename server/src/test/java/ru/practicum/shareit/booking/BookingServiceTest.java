package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.data.domain.PageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@AutoConfigureTestDatabase
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private BookingService bookingService;
    private User user;
    private User owner;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    public void setUp() {
        bookingService = new BookingServiceImpl(itemRepository, userRepository, bookingRepository);
        user = new User();
        user.setName("TestUser1");
        user.setEmail("Test@mail1.ru");
        owner = new User();
        owner.setName("TestOwner");
        owner.setEmail("TestOwner@mail.ru");
        Item item = new Item();
        item.setOwner(owner);
        item.setIsAvailable(true);
        item.setName("TestItem1");
        item.setDescription("TestItem2");
        userRepository.save(user);
        userRepository.save(owner);
        itemRepository.save(item);
        bookingRequestDto = new BookingRequestDto(item.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(1));
    }

    @Test
    void createBookingTest() {
        BookingDto result = bookingService.create(bookingRequestDto, user.getId());

        assertThat(result, notNullValue());
        assertThat(result.getItem().getId(), equalTo(bookingRequestDto.getItemId()));
    }

    @Test
    void updateBookingTest() {
        Long bookingId = bookingService.create(bookingRequestDto, user.getId()).getId();
        BookingDto result = bookingService.update(bookingId, owner.getId(), true);

        assertThat(result, notNullValue());
        assertThat(result.getStatus(), equalTo(Status.APPROVED));
    }

    @Test
    void findByIdTest() {
        Long bookingId = bookingService.create(bookingRequestDto, user.getId()).getId();
        BookingDto result = bookingService.findById(user.getId(), bookingId);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(bookingId));
    }

    @Test
    void findAllByBookerTest() {
        bookingService.create(bookingRequestDto, user.getId());
        List<BookingDto> result = bookingService.findAllByBooker(user.getId(), "ALL", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void findAllByOwnerTest() {
        bookingService.create(bookingRequestDto, user.getId());
        List<BookingDto> result = bookingService.findAllByOwner(owner.getId(), "ALL", PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }
}
