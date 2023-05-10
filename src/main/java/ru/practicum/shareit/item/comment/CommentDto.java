package ru.practicum.shareit.item.comment;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class CommentDto {
    @NotBlank
    private String text;
}
