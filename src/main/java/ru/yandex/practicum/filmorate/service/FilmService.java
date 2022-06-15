package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.films.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Getter
@Slf4j
public class FilmService {

    private final UserService userService;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService (@Lazy UserService userService, FilmStorage filmStorage) {
        this.userService = userService;
        this.filmStorage = filmStorage;
    }

    public Collection<Film> findAll () {
        return filmStorage.getFilms ();
    }

    public Film create (Film film) throws ValidationException {
        if (validationForFilm (film)) {
            filmStorage.createFilm (film);
        }
        return film;
    }

    public Film put (Film film) throws ValidationException {
        if (validationForFilm (film)) {
            filmStorage.updateFilm (film);
        }
        return film;
    }

    public void deleteFilm (Film film) throws NotFoundException {
        filmStorage.deleteFilm (film.getId ());
    }

    public void addLike (int userId, int filmId) throws NotFoundException {
        if (filmStorage.getFilmWithId (filmId) != null || userService.getUserById (userId) != null)
            filmStorage.getFilmWithId (filmId).getLikes ().add (userId);
    }

    public void removeLike (int filmId, int userId) throws NotFoundException {
        if (!userService.getAllUsersID ().contains (userId)) {
            throw new NotFoundException ("Произошла ошибка, пользователь с Ид "+ userId + " не найден");
        }
        filmStorage.getFilmWithId (filmId).getLikes ().remove (userId);
    }

    public List<Film> getPopularFilms (Integer count) {
        if (count == null) {
            count = 10;
        }
        return filmStorage.getFilms ()
                .stream ()
                .sorted (Comparator.comparingInt (film -> film.getLikes ().size ()))
                .limit (count)
                .collect (Collectors.toList ());
    }

    public Film getFilmById (int id) throws NotFoundException {
        return filmStorage.getFilmWithId (id);
    }

    private boolean validationForFilm (Film film) {
        if (film.getName () == null || film.getName ().isBlank () || film.getName ().isEmpty ()) {
            throw new ValidationException ("Название фильма не может быть пустым");
        } else if (film.getDescription ().length () > 200) {
            throw new ValidationException ("Максимальная длина описания — 200 символов");
        } else if (film.getReleaseDate ().isBefore (LocalDate.of (1895, 12, 28))) {
            throw new ValidationException ("Дата релиза — не раньше 28 декабря 1895 года");
        } else if (film.getDuration () <= 0) {
            throw new ValidationException ("Продолжительность фильма должна быть положительной");
        }
        return true;
    }
}
