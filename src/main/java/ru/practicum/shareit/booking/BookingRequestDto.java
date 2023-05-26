package ru.practicum.shareit.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
@ToString
public class BookingRequestDto {

    @NonNull
    private Long itemId;

    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;

    @NotNull
    @FutureOrPresent
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;
}