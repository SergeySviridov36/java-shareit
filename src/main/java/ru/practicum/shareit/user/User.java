package ru.practicum.shareit.user;

import lombok.*;

import javax.validation.constraints.Email;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class User {
    private Long id;
    private String name;
    @Email
    private String email;
}
