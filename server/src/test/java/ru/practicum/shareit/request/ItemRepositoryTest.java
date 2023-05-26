package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    public void createEnvironment() {
        user = new User();
        user.setName("userNameTest");
        user.setEmail("userEmailTest@mail.ru");
        userRepository.save(user);
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("itemRequestDescriptionTest");
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(testEntityManager);
    }

    @Test
    void findAllByRequestor_IdTest() {
        Sort sort = Sort.by("created").descending();
        List<ItemRequest> result = itemRequestRepository.findAllByRequestor_Id(user.getId(), sort);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void findAllByRequestor_IdNotTest() {
        Page<ItemRequest> result = itemRequestRepository.findAllByRequestor_IdNot(user.getId(), PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
    }
}
