package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.utility.IdUpdater;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
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
        if (film == null || films.containsKey (film.getId ())) {
            throw new ValidationException ("Произошла ошибка при создании фильма, введите корректные данные");
        }
        film.setId (idUpdater.updateId (film.getId (), films.keySet ()));
        films.put (film.getId (), film);
        log.info ("Фильм " + film + " Успешно добавлен");
        return film;
    }

    @PutMapping
    public Film put (@Valid @RequestBody Film film) throws ValidationException {
        if (film == null || !films.containsKey (film.getId ())) {
            throw new ValidationException ("Произошла ошибка при обновлении фильма, введите корректные данные");
        }
        films.put (film.getId (), film);
        log.info ("Фильм " + film + " Успешно обновлен");
        return film;
    }
}
