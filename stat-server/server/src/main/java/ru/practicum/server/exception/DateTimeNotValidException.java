package ru.practicum.server.exception;

public class DateTimeNotValidException extends RuntimeException {
    public DateTimeNotValidException(String message) {
        super(message);
    }
}
