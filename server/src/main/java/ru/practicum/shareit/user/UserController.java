package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping()
    public UserDto create(@RequestBody UserDto inputUserDto) {
        UserDto userDto = userService.create(inputUserDto);
        log.debug("Создание пользователя: {}", inputUserDto);
        return userDto;
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        List<UserDto> usersDto = userService.findAllUsers();
        log.debug("Получение списка пользователей");
        return usersDto;
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable Long userId) {
        UserDto userDto = userService.findUserById(userId);
        log.debug("Получение пользователя по id: {}", userId);
        return userDto;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        log.debug("Пользователь с идентификатором: " + userId + " удален.");
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto inputUser, @PathVariable Long userId) {
        UserDto userDto = userService.updateUser(userId, inputUser);
        log.debug("Обновление пользователя: {}", userId);
        return userDto;
    }
}


