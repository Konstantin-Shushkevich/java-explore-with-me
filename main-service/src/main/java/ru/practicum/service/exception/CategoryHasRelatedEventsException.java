package ru.practicum.service.exception;

public class CategoryHasRelatedEventsException extends RuntimeException {
    public CategoryHasRelatedEventsException(String message) {
        super(message);
    }
}
