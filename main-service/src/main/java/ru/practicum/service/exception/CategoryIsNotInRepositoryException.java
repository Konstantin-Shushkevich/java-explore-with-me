package ru.practicum.service.exception;

public class CategoryIsNotInRepositoryException extends RuntimeException {
    public CategoryIsNotInRepositoryException(String message) {
        super(message);
    }
}
