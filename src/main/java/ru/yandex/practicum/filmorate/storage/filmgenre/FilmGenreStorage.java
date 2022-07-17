package ru.yandex.practicum.filmorate.storage.filmgenre;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Set;
@Repository
public interface FilmGenreStorage {
    Set<Genre> findAllWithFilmId (int id);

    void addNewGenreToFilm (int filmId, Genre genre);

    void updateAllGenresForFilm (Film film);
}
