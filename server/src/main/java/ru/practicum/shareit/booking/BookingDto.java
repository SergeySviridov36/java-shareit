package ru.practicum.shareit.booking;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private Booker booker;
    private Status status;

    @Data
    @AllArgsConstructor
    public static class Booker {
        private Long id;
        private String name;
    }

    @Data
    @AllArgsConstructor
    public static class Item {
        private Long id;
        private String name;
    }
}
