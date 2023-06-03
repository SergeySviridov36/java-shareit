package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.UserMapper.userDtoInUser;
import static ru.practicum.shareit.user.UserMapper.userInDTO;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::userInDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto create(UserDto inputUserDto) {
        User user = userRepository.save(userDtoInUser(inputUserDto));
        return userInDTO(user);
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserDto findUserById(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return userInDTO(user.orElseThrow(() -> new NotFoundException("Пользователь не найден")));
    }

    @Transactional
    @Override
    public UserDto updateUser(Long userId, UserDto inputUserDto) {
        final User newUser = userDtoInUser(inputUserDto);
        newUser.setId(userId);
        User oldUser = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (newUser.getName() == null)
            newUser.setName(oldUser.getName());
        if (newUser.getEmail() == null)
            newUser.setEmail(oldUser.getEmail());
        return userInDTO(userRepository.save(newUser));
    }
}
