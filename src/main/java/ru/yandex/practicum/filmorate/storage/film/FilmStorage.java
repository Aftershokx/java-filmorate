package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
public interface FilmStorage {

    void create (Film film) throws RuntimeException;

    void update (Film film) throws NotFoundException;

    void delete (int id) throws RuntimeException;

    List<Film> getFilms() throws RuntimeException;

    Film getFilmWithId (int id) throws RuntimeException;


}
