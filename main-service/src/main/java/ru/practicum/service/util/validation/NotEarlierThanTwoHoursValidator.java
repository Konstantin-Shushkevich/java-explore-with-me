package ru.practicum.service.util.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class NotEarlierThanTwoHoursValidator implements
        ConstraintValidator<NotEarlierThanTwoHours, LocalDateTime> {

    private int hoursOffset;

    @Override
    public void initialize(NotEarlierThanTwoHours constraintAnnotation) {
        this.hoursOffset = constraintAnnotation.hours();
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minAllowedTime = now.plusHours(hoursOffset);

        return value.isAfter(minAllowedTime);
    }
}
