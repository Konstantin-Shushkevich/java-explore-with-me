package ru.practicum.service.exception;

public class CommentIsNotInRepositoryException extends RuntimeException {
    public CommentIsNotInRepositoryException(String message) {
        super(message);
    }
}
