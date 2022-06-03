package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utility.IdUpdater;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<> ();
    private final IdUpdater idUpdater = new IdUpdater ();

    @GetMapping
    public Collection<Film> findAll () {
        return films.values ();
    }

    @PostMapping
    public Film create (@Valid @RequestBody Film film) throws ValidationException {
        validationForFilm (film);
        if (films.containsKey (film.getId ())) {
            throw new ValidationException ("Произошла ошибка при создании фильма, фильм с таким ИД уже существует");
        }
        film.setId (idUpdater.updateId (film.getId (), films.keySet ()));
        films.put (film.getId (), film);
        log.info ("Фильм " + film + " Успешно добавлен");
        return film;
    }

    @PutMapping
    public Film put (@Valid @RequestBody Film film) throws ValidationException {
        validationForFilm (film);
        if (!films.containsKey (film.getId ())) {
            throw new ValidationException
                    ("Произошла ошибка при обновлении фильма, фильм с таким ИД не существует");
        }
        films.put (film.getId (), film);
        log.info ("Фильм " + film + " Успешно обновлен");
        return film;
    }

    private void validationForFilm (Film film) {
        if (film.getName () == null || film.getName ().isBlank () || film.getName ().isEmpty ()) {
            throw new ValidationException ("Название фильма не может быть пустым");
        } else if (film.getDescription ().length () > 200) {
            throw new ValidationException ("Максимальная длина описания — 200 символов");
        } else if (film.getReleaseDate ().isBefore (LocalDate.of (1895, 12, 28))) {
            throw new ValidationException ("Дата релиза — не раньше 28 декабря 1895 года");
        } else if (film.getDuration () <= 0) {
            throw new ValidationException ("Продолжительность фильма должна быть положительной");
        }
    }

}
