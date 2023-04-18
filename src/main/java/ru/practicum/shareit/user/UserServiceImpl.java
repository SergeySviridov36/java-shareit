package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidateException;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.UserMapper.userDtoInUser;
import static ru.practicum.shareit.user.UserMapper.userInDTO;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> findAllUsers() {
        return userRepository.findAllUsers().stream().map(UserMapper::userInDTO).collect(Collectors.toList());
    }

    @Override
    public UserDto create(UserDto inputUserDto) {
        checkingEmailCreating(inputUserDto);
        User user = userRepository.create(userDtoInUser(inputUserDto));
        userRepository.getListEmail().add(inputUserDto.getEmail());
        return userInDTO(user);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.getListEmail().remove(findUserById(userId).getEmail());
        userRepository.deleteUser(userId);
    }

    @Override
    public UserDto findUserById(Long userId) {
        User user = userRepository.findUserById(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        return userInDTO(user);
    }

    @Override
    public UserDto updateUser(Long userId, UserDto inputUserDto) {
        UserDto userDto = findUserById(userId);
        if (inputUserDto.getName() != null) {
            userDto.setName(inputUserDto.getName());
        }
        if (inputUserDto.getEmail() != null) {
            userRepository.getListEmail().remove(userDto.getEmail());
            checkingEmailCreating(inputUserDto);
            userDto.setEmail(inputUserDto.getEmail());
            userRepository.getListEmail().add(inputUserDto.getEmail());
        }
        User user = userDtoInUser(userDto);
        user.setId(userId);
        User userUpdate = userRepository.updateUser(user);
        return userInDTO(userUpdate);
    }

    private void checkingEmailCreating(User inputUser) {
        if (userRepository.getListEmail().contains(inputUser.getEmail()))
            throw new ValidateException("Ошибка!Такой Email уже зарегистрирован");
    }
}
