package ru.practicum.shareit.user;

import lombok.*;

import javax.validation.constraints.Email;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class UserDto extends User {
    private Long id;
    private String name;
    @Email
    private String email;
}
