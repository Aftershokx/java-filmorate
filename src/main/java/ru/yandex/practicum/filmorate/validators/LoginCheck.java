package ru.yandex.practicum.filmorate.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Retention(RUNTIME)
@Target({FIELD, ANNOTATION_TYPE, PARAMETER})
@Constraint(validatedBy = LoginCheckValidator.class)
public @interface LoginCheck {

    String message () default "Логин не может быть пустым и содержать пробелы";

    Class<?>[] groups () default {};

    Class<? extends Payload>[] payload () default {};
}