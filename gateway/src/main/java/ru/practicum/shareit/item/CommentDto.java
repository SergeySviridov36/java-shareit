package ru.practicum.shareit.item;

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