package ru.practicum.service.exception.handler;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.service.exception.*;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler({
            CategoryIsNotInRepositoryException.class,
            UserIsNotInRepositoryException.class,
            EventIsNotInRepositoryException.class,
            RequestNotFoundException.class,
            CommentIsNotInRepositoryException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFoundException(final RuntimeException e) {
        log.error("One of NotFoundExceptions was thrown");
        return ApiError.builder()
                .status(HttpStatus.NOT_FOUND.toString())
                .reason("The required object was not found.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler({
            IncorrectRequestException.class,
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            MissingServletRequestParameterException.class,
            InvalidCommentTimeFiltersRequest.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleIncorrectRequestException(final RuntimeException e) {
        log.error("IncorrectRequestException was thrown");
        return ApiError.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .reason("Incorrectly made request.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler({
            CategoryHasRelatedEventsException.class,
            CategoryNameNotUniqueException.class,
            ViolationOfTermsException.class,
            UserEmailAlreadyInRepositoryException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConflictException(final RuntimeException e) {
        log.error("One of ConflictExceptions was thrown");
        return ApiError.builder()
                .status(HttpStatus.CONFLICT.toString())
                .reason("Something wrong with your data.")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleException(final Exception e) {
        log.error("Exception was thrown");
        return ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.toString())
                .reason(e.getMessage())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
