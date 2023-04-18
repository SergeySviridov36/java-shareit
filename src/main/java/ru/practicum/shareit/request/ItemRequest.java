package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
public class ItemRequest {
    private Long id;
    private String requestor;
    private String description;
    private LocalDate created;
}
