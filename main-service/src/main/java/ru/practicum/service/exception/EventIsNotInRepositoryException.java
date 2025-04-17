package ru.practicum.service.exception;

public class EventIsNotInRepositoryException extends RuntimeException {
    public  EventIsNotInRepositoryException(String message) {
        super(message);
    }
}
