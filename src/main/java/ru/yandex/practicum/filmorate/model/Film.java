package ru.yandex.practicum.filmorate.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.validators.LocalDateCheck;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {
    private int id;
    @NotBlank(message = "Строка имени не должна быть пустой")
    private String name;
    @Length(max = 200, message = "Длина описания не должна превышать 200 символов")
    private String description;
    @LocalDateCheck()
    private LocalDate releaseDate;
    @Min(value = 0, message = "Продолжительность должна иметь положительное значение")
    private long duration;
    private Set<Integer> likes = new HashSet<> ();
    private List<Genre> genres;
    @NotNull
    private Mpa mpa;
}
