package ru.practicum.shareit.user;

import lombok.*;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.Email;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class UserDto extends User {
    private Long id;
    private String name;
    @Email
    private String email;
}
