package ru.practicum.shareit.item.comment;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class CommentDto {
    @NotBlank
    private String text;
}
