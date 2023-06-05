package ru.practicum.shareit.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

import static ru.practicum.shareit.GlobalProperties.DATE_FORMAT;

public class FutureOrPresentStringValidator implements ConstraintValidator<FutureOrPresentString, String> {

    public void initialize(FutureOrPresentString parameters) {
        // Nothing to do here
    }

    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) return true;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime date = LocalDateTime.parse(value, DATE_FORMAT);
        return date.isAfter(now) || date.isEqual(now);
    }
}
