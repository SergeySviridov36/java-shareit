package ru.practicum.shareit.request;

import lombok.*;
import ru.practicum.shareit.item.ItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ItemRequestDto {
    private Long id;
    private List<ItemDto> items = new ArrayList<>();
    private String description;
    private LocalDateTime created;
}