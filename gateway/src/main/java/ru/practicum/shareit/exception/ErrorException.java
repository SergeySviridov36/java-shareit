package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ErrorException {
    private long timeException;
    private String error;

    public ErrorException(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
