package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlers {

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorException exceptionHandler(NotFoundEntityExeption notFoundEntityExeption) {
        log.debug(notFoundEntityExeption.getMessage());
        return new ErrorException(System.currentTimeMillis(), notFoundEntityExeption.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ErrorException exceptionHandler(NotFoundException notFoundException) {
        log.debug(notFoundException.getMessage());
        return new ErrorException(System.currentTimeMillis(), notFoundException.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorException handleThrowable(final Throwable e) {
        log.warn(e.getMessage());
        return new ErrorException(System.currentTimeMillis(), e.getMessage());
    }
}