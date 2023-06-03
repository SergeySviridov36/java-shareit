package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid UserDto userDto) {
        log.info("Creating user {}", userDto);
        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto,
                                             @PathVariable Long userId) {
        log.info("Получен PATCH-запрос  обновления пользователя с ID={}", userId);
        return userClient.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable Long userId) {
        log.info("Получен PATCH-запрос получение пользователя с ID={}", userId);
        return userClient.getUser(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUsers() {
        log.info("Получен PATCH-запрос получение всех пользователей");
        return userClient.getUsers();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long userId) {
        log.info("Получен PATCH-запрос удаления пользователя");
        return userClient.deleteUser(userId);
    }
}