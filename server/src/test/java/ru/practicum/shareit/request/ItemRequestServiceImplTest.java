package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import static ru.practicum.shareit.request.ItemRequestMapper.dtoInRequest;


@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {
    @Mock
    private final ItemRequestRepository itemRequestRepository;
    @Mock
    private final UserRepository userRepository;
    @Mock
    private final ItemRepository itemRepository;
    private ItemRequestService itemRequestService;
    private ItemRequestDto itemRequestDto;
    private User user;

    @BeforeEach
    public void addEnvironsForAll() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
        user = new User();
        user.setId(1L);
        user.setName("userNameTest");
        user.setEmail("userEmailTest@mail.ru");
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("itemRequestDtoDescriptionTest");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when((userRepository.existsById(anyLong())))
                .thenReturn(Boolean.valueOf("true"));
    }

    @Test
    void createItemRequestTest() {
        ItemRequestDto result = itemRequestService.create(itemRequestDto, 1L);

        assertThat(result, notNullValue());
        assertThat(result.getDescription(), equalTo(itemRequestDto.getDescription()));
        verify(userRepository, times(1)).findById(1L);
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void findAllRequestByOwnerTest() {
        when(itemRequestRepository.findAllByRequestor_Id(anyLong(), any()))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> result = itemRequestService.findAllRequestByOwner(1L);

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(itemRequestRepository, times(1)).findAllByRequestor_Id(anyLong(), any());
        verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    void findAllRequestTest() {
        when(itemRequestRepository.findAllByRequestor_IdNot(anyLong(), any()))
                .thenReturn(Page.empty());

        List<ItemRequestDto> result = itemRequestService.findAllRequest(1L, PageRequest.of(0, 10));

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
        verify(itemRequestRepository, times(1)).findAllByRequestor_IdNot(anyLong(), any());
        verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    void findItemRequestByRequestIdTest() {
        ItemRequest itemRequest = dtoInRequest(itemRequestDto, user);

        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(Collections.emptyList());

        ItemRequestDto result = itemRequestService.findByRequestId(1L, 1L);

        assertThat(result, notNullValue());
        verify(itemRequestRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findAllByRequestId(1L);
        verify(userRepository, times(1)).existsById(1L);
    }
}
