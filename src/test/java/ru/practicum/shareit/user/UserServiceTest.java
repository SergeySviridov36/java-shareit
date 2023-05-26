package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@AutoConfigureTestDatabase
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private UserService userService;

    private final UserRepository userRepository;
    private UserDto userDto;

    @BeforeEach
    public void addEnvironsForAll() {
        userService = new UserServiceImpl(userRepository);
        userDto = new UserDto();
        userDto.setName("userDtoNameTest");
        userDto.setEmail("userDtoEmailTest@mail.ru");
    }

    @Test
    void createUserTest() {
        UserDto result = userService.create(userDto);

        assertThat(result, notNullValue());
        assertThat(result.getName(), equalTo(userDto.getName()));
        assertThat(result.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void findUserByIdTest() {
        long userId = userService.create(userDto).getId();
        UserDto result = userService.findUserById(userId);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(userId));
    }

    @Test
    void updateUserTest() {
        long userId = userService.create(userDto).getId();
        userDto.setName("Alex");
        UserDto result = userService.updateUser(userId, userDto);

        assertThat(result, notNullValue());
        assertThat(result.getName(), equalTo("Alex"));
    }

    @Test
    void deleteUserTest() {
        long userId = userService.create(userDto).getId();
        userService.deleteUser(userId);

        Assertions.assertThrows(NotFoundException.class, () -> userService.findUserById(userId));
    }

    @Test
    void findAllUsersTest() {
        List<UserDto> result = userService.findAllUsers();

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
    }
}
