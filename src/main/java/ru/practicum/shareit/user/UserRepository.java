package ru.practicum.shareit.user;

import java.util.List;
import java.util.Set;

public interface UserRepository {

    List<User> findAllUsers();

    User create(User user);

    void deleteUser(Long userId);

    User findUserById(Long userId);

    User updateUser(User userDtoInUser);

    Set<String> getListEmail();
}
