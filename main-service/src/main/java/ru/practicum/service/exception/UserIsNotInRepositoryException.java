package ru.practicum.service.exception;

public class UserIsNotInRepositoryException extends RuntimeException {
    public UserIsNotInRepositoryException(String message) {
        super(message);
    }
}
