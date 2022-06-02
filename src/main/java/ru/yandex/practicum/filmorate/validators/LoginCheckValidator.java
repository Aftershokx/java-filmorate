package ru.yandex.practicum.filmorate.validators;

import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


@Component
public class LoginCheckValidator implements ConstraintValidator<LoginCheck, String> {

    @Override
    public boolean isValid (String s, ConstraintValidatorContext constraintValidatorContext) {
        return !s.contains (" ") && !s.isBlank ();
    }
}
