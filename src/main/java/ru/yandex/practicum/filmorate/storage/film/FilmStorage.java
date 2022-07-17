package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Repository
public interface FilmStorage {

    Film create (Film film) throws RuntimeException;

    Film update (Film film) throws NotFoundException;

    void delete (int id) throws RuntimeException;

    List<Film> getFilms () throws RuntimeException;

    Film getFilmWithId (int id) throws RuntimeException;

    List<Film> getMostPopular (Integer count);

    void insertLikes (Film film);

    void deleteLikes (Film film);

    void deleteLike (int filmId, int userId);


}
