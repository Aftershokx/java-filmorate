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
@Constraint(validatedBy = LocalDateCheckValidator.class)
public @interface LocalDateCheck {

    String message () default "Указанная дата релиза не может быть раньше 28 декабря 1895";

    Class<?>[] groups () default {};

    Class<? extends Payload>[] payload () default {};
}
