package ru.practicum.server.exception.handler;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ErrorResponse {
    private final String error;
    private final String description;
}
