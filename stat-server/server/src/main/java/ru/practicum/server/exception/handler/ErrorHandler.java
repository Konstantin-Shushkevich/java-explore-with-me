package ru.practicum.server.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.server.exception.DateTimeNotValidException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({DateTimeNotValidException.class, MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectRequestException(final RuntimeException e) {
        log.error("Incorrectly made request.");
        return new ErrorResponse("Incorrect request", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(final Exception e) {
        log.error("Exception was thrown");
        return new ErrorResponse("Something went wrong", e.getMessage());
    }
}
