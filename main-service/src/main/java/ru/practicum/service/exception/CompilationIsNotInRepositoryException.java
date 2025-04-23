package ru.practicum.service.exception;

public class CompilationIsNotInRepositoryException extends RuntimeException {
    public CompilationIsNotInRepositoryException(String message) {
        super(message);
    }
}
