package ru.practicum.shareit.booking;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
@ToString
public class BookingRequestDto {

    private Long itemId;

    private LocalDateTime start;

    private LocalDateTime end;
}