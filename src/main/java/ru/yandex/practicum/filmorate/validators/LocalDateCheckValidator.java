package ru.yandex.practicum.filmorate.validators;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

@Component
public class LocalDateCheckValidator implements ConstraintValidator<LocalDateCheck, LocalDate> {
    private static final LocalDate MIN_DATE = LocalDate.of (1895, 12, 28);

    @Override
    public boolean isValid (LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        return localDate.isEqual (MIN_DATE) || localDate.isAfter (MIN_DATE);
    }

}

