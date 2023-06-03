package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static ru.practicum.shareit.request.ItemRequestMapper.dtoInRequest;


@Transactional
@AutoConfigureTestDatabase
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private ItemRequestServiceImpl itemRequestService;
    private ItemRequestDto itemRequestDto;
    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    public void addEnvironsForAll() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
        user = new User();
        user.setName("userNameTest");
        user.setEmail("userEmailTest@mail.ru");
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("itemRequestDtoDescriptionTest");
        Item item = new Item();
        item.setOwner(user);
        item.setIsAvailable(Boolean.TRUE);
        item.setName("itemName");
        item.setDescription("itemDescription");
        userRepository.save(user);
        itemRequest = dtoInRequest(itemRequestDto, user);
        var itemRequestResult = itemRequestRepository.save(itemRequest);
        item.setRequest(itemRequestResult);
        itemRepository.save(item);
    }

    @Test
    void createItemRequestTest() {
        ItemRequestDto result = itemRequestService.create(itemRequestDto, user.getId());

        assertThat(result, notNullValue());
        assertThat(result.getDescription(), equalTo("itemRequestDtoDescriptionTest"));
    }

    @Test
    void findAllRequestByOwnerTest() {
        List<ItemRequestDto> result = itemRequestService.findAllRequestByOwner(user.getId());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void findAllRequestTest() {
        User userNew = new User();
        userNew.setName("userNewSetName");
        userNew.setEmail("userNewSetEmail@mail.ru");
        User userResult = userRepository.save(userNew);
        List<ItemRequestDto> result = itemRequestService.findAllRequest(userResult.getId(), PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void findByRequestIdTest() {
        ItemRequestDto result = itemRequestService.findByRequestId(user.getId(), itemRequest.getId());

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(itemRequest.getId()));
        assertThat(result.getItems().size(), equalTo(1));
    }
}
