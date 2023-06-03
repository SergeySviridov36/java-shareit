package ru.practicum.shareit.user;

import lombok.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class UserDto extends User {
    private Long id;
    private String name;
    private String email;
}
