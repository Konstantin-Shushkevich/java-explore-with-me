package ru.practicum.service.exception;

public class InvalidCommentTimeFiltersRequest extends RuntimeException {
    public InvalidCommentTimeFiltersRequest(String message) {
        super(message);
    }
}
