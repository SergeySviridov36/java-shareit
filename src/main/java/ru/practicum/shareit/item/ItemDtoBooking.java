package ru.practicum.shareit.item;

import lombok.*;
import ru.practicum.shareit.item.comment.CommentDtoResponse;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ItemDtoBooking {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long request;
    private User owner;
    private List<CommentDtoResponse> comments;
    private BookingItemDto lastBooking;
    private BookingItemDto nextBooking;

    public ItemDtoBooking(Long id, String name, String description, Boolean available,User owner, Long request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    @ToString
    public static class BookingItemDto {
        private Long id;
        private LocalDateTime start;
        private LocalDateTime end;
        private Long bookerId;
    }
}
