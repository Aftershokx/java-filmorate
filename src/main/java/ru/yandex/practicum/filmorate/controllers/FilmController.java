package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.service.user.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;
    private final UserService userService;

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film getFilmWithId(@PathVariable int id) throws RuntimeException {
        return filmService.getWithId(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false) Integer count) throws RuntimeException {
        return filmService.getPopularFilms(count);
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) throws ValidationException {
        if (filmService.validation(film)) {
            filmService.create(film);
        }
        return film;
    }

    @DeleteMapping
    public void delete(@Valid @RequestBody Film film) throws RuntimeException {
        filmService.delete(film);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable int filmId, @PathVariable int userId) throws NotFoundException {
        if (!userService.getAllUsersID().contains(userId)) {
            throw new NotFoundException("Произошла ошибка, пользователь с Ид " + userId + " не найден");
        }
        filmService.removeLike(filmId, userId);
    }

    @PutMapping
    public Film put(@Valid @RequestBody Film film) throws ValidationException {
        if (filmService.validation(film)) {
            filmService.put(film);
        }
        return film;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) throws RuntimeException {
        if (filmService.getWithId(id) != null || userService.getWithId(userId) != null) {
            filmService.addLike(id, userId);
        }
    }
}
