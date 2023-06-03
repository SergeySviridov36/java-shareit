package ru.practicum.shareit.item.comment;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class CommentDtoResponse {
    private Long id;
    private String text;
    private String authorName;
    private LocalDateTime created;
}
