package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validators.LocalDateCheck;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class Film {
    private int id;
    @NotNull
    @NotBlank(message = "Строка имени не должна быть пустой")
    private String name;
    @Length(max = 200, message = "Длина описания не должна превышать 200 символов")
    private String description;
    @LocalDateCheck()
    private LocalDate releaseDate;
    @Min(value = 0, message = "Продолжительность должна иметь положительное значение")
    private long duration;

}
