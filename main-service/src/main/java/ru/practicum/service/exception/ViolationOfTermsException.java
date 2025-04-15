package ru.practicum.service.exception;

public class ViolationOfTermsException extends RuntimeException {
    public ViolationOfTermsException(String message) {
        super(message);
    }
}
