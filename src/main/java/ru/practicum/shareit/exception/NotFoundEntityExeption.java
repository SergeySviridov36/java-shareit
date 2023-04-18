package ru.practicum.shareit.exception;

public class NotFoundEntityExeption extends RuntimeException {
    public NotFoundEntityExeption(String message) {
        super(message);
    }
}

