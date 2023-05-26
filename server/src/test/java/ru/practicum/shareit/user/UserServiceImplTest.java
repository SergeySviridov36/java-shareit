package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.user.UserMapper.userDtoInUser;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {
    @Mock
    private final UserRepository userRepository;
    private UserService userService;
    private UserDto userDto;

    @BeforeEach
    public void addEnvironsForAll() {
        userService = new UserServiceImpl(userRepository);
        userDto = new UserDto();
        userDto.setName("userNameTest");
        userDto.setEmail("userEmailTest@mail.ru");

        when(userRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void exceptionWhenEmailNullTest() {
        userDto.setEmail(null);

        verify(userRepository, times(0)).save(userDtoInUser(userDto));
        Assertions.assertThrows(NotFoundException.class, () -> userService.create(userDto));
    }

    @Test
    void createUserTest() {
        UserDto result = userService.create(userDto);

        assertThat(result, notNullValue());
        assertThat(result.getName(), equalTo(userDto.getName()));
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void findUserByIdTest() {
        long userId = 99L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        verify(userRepository, times(0)).findById(userId);
        Assertions.assertThrows(NotFoundException.class, () -> userService.findUserById(userId));
    }

    @Test
    void exceptionWhenNameOrEmailIfTheyAreNull() {
        UserDto result = userService.create(userDto);
        userDto.setEmail(null);
        userDto.setName(null);
        long userId = 1L;

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userDtoInUser(result)));
        UserDto resultUpdate = userService.updateUser(userId, userDto);

        assertThat(resultUpdate, notNullValue());
        assertThat(resultUpdate.getName(), equalTo(result.getName()));
        assertThat(resultUpdate.getEmail(), equalTo(result.getEmail()));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void findAllUsersTest() {
        when(userRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<UserDto> result = userService.findAllUsers();

        assertThat(result, notNullValue());
        assertThat("isEmpty", result.isEmpty());
    }
}
