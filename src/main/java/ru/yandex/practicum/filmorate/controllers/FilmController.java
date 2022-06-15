package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final FilmService filmService;

    @Autowired
    public FilmController (FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> findAll () {
        return filmService.findAll ();
    }

    @GetMapping("/{id}")
    public Film getFilmWithId (@PathVariable int id) throws RuntimeException {
        return filmService.getFilmById (id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms (@RequestParam(required = false) Integer count) throws RuntimeException {
        return filmService.getPopularFilms (count);
    }

    @PostMapping
    public Film create (@Valid @RequestBody Film film) throws ValidationException {
        return filmService.create (film);
    }

    @DeleteMapping
    public void delete (@Valid @RequestBody Film film) throws RuntimeException {
        filmService.deleteFilm (film);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike (@PathVariable int filmId, @PathVariable int userId) throws NotFoundException {
        filmService.removeLike (filmId, userId);
    }

    @PutMapping
    public Film put (@Valid @RequestBody Film film) throws ValidationException {
        return filmService.put (film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike (@PathVariable int id, @PathVariable int userId) throws RuntimeException {
        filmService.addLike (id, userId);
    }
}
