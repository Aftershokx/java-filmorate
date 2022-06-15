package ru.yandex.practicum.filmorate.storage.films;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
public interface FilmStorage {

    void createFilm (Film film) throws RuntimeException;

    void updateFilm (Film film) throws NotFoundException;

    void deleteFilm (int id) throws RuntimeException;

    List<Film> getFilms() throws RuntimeException;

    Film getFilmWithId (int id) throws RuntimeException;


}
