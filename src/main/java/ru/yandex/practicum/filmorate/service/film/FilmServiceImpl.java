package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
@Service
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;

    @Override
    public Collection<Film> findAll() {
        return filmStorage.getFilms();
    }

    @Override
    public Film create(Film film) throws ValidationException {
        filmStorage.create(film);
        return film;
    }

    @Override
    public Film put(Film film) throws ValidationException {
        filmStorage.update(film);
        return film;
    }

    @Override
    public void delete(Film film) throws NotFoundException {
        filmStorage.delete(film.getId());
    }

    @Override
    public Film getWithId(int id) throws NotFoundException {
        return filmStorage.getFilmWithId(id);
    }

    @Override
    public boolean validation(Film film) {
        if (film.getName() == null || film.getName().isBlank() || film.getName().isEmpty()) {
            throw new ValidationException("Название фильма не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        } else if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        return true;
    }

    @Override
    public void addLike(int filmId, int userId) throws NotFoundException {
        Film film = filmStorage.getFilmWithId(filmId);
        film.getLikes().add(userId);
        filmStorage.insertLikes(film);

    }

    @Override
    public void removeLike(int filmId, int userId) throws NotFoundException {
        Film film = filmStorage.getFilmWithId(filmId);
        film.getLikes().remove(userId);
        filmStorage.deleteLike(filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        if (count == null) {
            count = 10;
        }
        return filmStorage.getMostPopular(count);
    }

}
