package ru.practicum.service.exception;

public class UserEmailAlreadyInRepositoryException extends RuntimeException {
    public UserEmailAlreadyInRepositoryException(String message) {
        super(message);
    }
}
