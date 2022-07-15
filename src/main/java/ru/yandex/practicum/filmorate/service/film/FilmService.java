package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {

    Film create (Film film) throws ValidationException;

    Film put (Film film) throws ValidationException;

    void delete (Film film) throws NotFoundException;

    Collection<Film> findAll ();

    Film getWithId (int id) throws NotFoundException;

    boolean validation (Film film);

    void addLike (int userId, int filmId) throws NotFoundException;

    void removeLike (int filmId, int userId) throws NotFoundException;

    List<Film> getPopularFilms (Integer count);

}
